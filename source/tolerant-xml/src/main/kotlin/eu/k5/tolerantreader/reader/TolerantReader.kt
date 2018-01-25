package eu.k5.tolerantreader.reader

import eu.k5.tolerantreader.tolerant.*
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent

enum class ViolationType {
    TYPE_MISMATCH,

    UNKNOWN_CONTENT,

    MISSING_ENTITY,

    INVALID_ENUM_LITERAL,

    INVALID_CLASS_STRUCTURE

}

data class Violation(
        val type: ViolationType,
        val description: String,
        val location: String?
)

data class ReadResult(
        val violations: List<Violation>,
        val instance: Any?
)


class TolerantReader(val schema: TolerantSchema) {


    fun read(stream: XMLStreamReader, readerConfig: TolerantReaderConfiguration): ReadResult {

        val context = schema.createContext(readerConfig)


        read(context, stream)

        context.postProcess()

        return context.createResult()
    }

    private fun read(context: BindContext, stream: XMLStreamReader) {
        while (stream.hasNext()) {
            val event = stream.next()

            if (XMLEvent.START_ELEMENT == event) {

                val qName = stream.name


                val element =
                        if (context.isEmpty()) {
                            schema.getElement(qName.namespaceURI, qName.localPart)
                        } else {
                            context.getElement(qName.namespaceURI, qName.localPart)
                        }

                if (element == null) {
                    val transformer = context.getTransformer(qName.localPart)
                    if (transformer != null) {
                        val replay = Replay.record(transformer.target, stream)
                        context.pushReplay(replay)
                    } else if (context.keepFrame() && context.isEmpty()) {
                        context.pushFrameElement(stream)
                    } else {
                        context.addViolation(ViolationType.UNKNOWN_CONTENT, "Element: " + qName)
                        skipToEndElement(context, stream)
                    }
                    continue
                }
                context.pushName(qName)


                val type = element.type.asSubtype(context, stream)


                val readValue = type.readValue(context, element, context.getCurrentInstance(), stream)
                if (readValue != null) {

                    element.assigner.assign(context, context.getCurrentInstance()!!, readValue)

                    if (type.pushedOnStack()) {
                        context.push(element, readValue, type)
                    } else {
                        context.popName()
                    }
                } else {
                    context.popName()
                }
            } else if (XMLEvent.COMMENT == event) {
                context.addComment(stream.text)
            } else if (XMLEvent.CHARACTERS == event) {
                if (context.keepCharacters()) {
                    context.addCharacters(stream.text)
                }


            } else if (XMLEvent.END_ELEMENT == event) {
                if (context.isEmpty()) {
                    context.popFrame()
                } else {
                    val replay = context.retrieveReplay()

                    if (replay != null) {
                        doReplay(context, replay)
                    }
                    context.pop()
                    context.popName()
                }
            }
        }
    }

    private fun doReplay(context: BindContext, replays: Map<String, Replay>) {
        for ((elementName, replay) in replays) {

            val stream = replay.asStreamReader()

            read(context, stream)

        }
    }


    private fun skipToEndElement(context: BindContext, stream: XMLStreamReader) {
        var balance = 1
        while (stream.hasNext()) {
            val event = stream.next()

            if (XMLEvent.START_ELEMENT == event) {

                balance++
            } else if (XMLEvent.END_ELEMENT == event) {
                balance--
                if (balance == 0) {
                    return
                }
            } else if (XMLEvent.END_DOCUMENT == event) {
                return
            }
        }

    }
}

