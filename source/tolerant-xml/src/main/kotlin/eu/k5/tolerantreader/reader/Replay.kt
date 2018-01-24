package eu.k5.tolerantreader.reader

import java.util.*
import javax.xml.namespace.NamespaceContext
import javax.xml.namespace.QName
import javax.xml.stream.Location
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent


class Replay(
        val qName: QName,
        val parent: Replay?,
        val attributes: List<ReplayAttribute>
) {

    val children = ArrayList<Replay>()

    var replayText: String? = null

    var childrenDepth = 1

    init {
        if (parent != null) {
            parent.children.add(this)
        }
    }

    fun asStreamReader(): XMLStreamReader {
        return ReplayStream(this, childrenDepth)
    }

    fun getRoot(): Replay {
        return parent?.getRoot() ?: this
    }

    companion object {

        private fun createRoot(targetPath: Array<QName>, stream: XMLStreamReader): Replay {
            var root: Replay? = null
            for (index in 0 until targetPath.size - 1) {
                root = Replay(targetPath[index], root, Collections.emptyList())
            }

            return Replay(targetPath.last(), root, readAttributes(stream))
        }

        fun record(targetPath: Array<QName>, stream: XMLStreamReader): Replay {

            var current = createRoot(targetPath, stream)

            val root = current.getRoot()


            // attributes

            var balance = 1
            while (stream.hasNext()) {
                val event = stream.next()

                if (XMLEvent.START_ELEMENT == event) {
                    balance++

                    current = Replay(stream.name, current, readAttributes(stream))

                } else if (XMLEvent.END_ELEMENT == event) {
                    balance--


                    if (balance == 0) {
                        return root
                    } else {

                        current = current.parent!!
                        if (current.children.size == 1) {
                            current.childrenDepth++
                        }
                    }
                } else if (XMLEvent.CHARACTERS == event) {
                    current.replayText = stream.text
                } else if (XMLEvent.END_DOCUMENT == event) {
                    break
                }
            }
            return root

        }

        private fun readAttributes(stream: XMLStreamReader): List<ReplayAttribute> {
            if (stream.attributeCount == 0) {
                return Collections.emptyList()
            }
            return (0 until stream.attributeCount).map {
                ReplayAttribute(name = stream.getAttributeLocalName(it), value = stream.getAttributeValue(it))
            }
        }

    }


}

class ReplayAttribute(
        val name: String,
        val value: String
)

class ReplayStream(
        replay: Replay,
        childrenDepth: Int
) : XMLStreamReader {

    private var current: Replay? = replay

    private var level = 0

    private var steps = Array(childrenDepth + 1) { -1 }

    init {
        if (childrenDepth > 0) {
            steps[0]--
        }
    }

    override fun hasNext(): Boolean {
        return 0 <= level
    }

    override fun getText(): String? {
        return current!!.replayText
    }

    override fun next(): Int {
        steps[level]++
        if (steps[level] == -1) {
            if (!current!!.children.isEmpty()) {
                steps[level]++
            }
            return XMLEvent.START_ELEMENT
        } else if (isEnd(current!!, steps[level])) {
            level--
            current = current!!.parent
            return XMLEvent.END_ELEMENT
        } else if (isCharacters(current!!, steps[level])) {
            return XMLEvent.CHARACTERS
        } else if (isStart(current!!, steps[level])) {
            current = current!!.children[steps[level] - 1]
            level++
            steps[level] = -1
            return XMLEvent.START_ELEMENT
        } else {

            TODO("consider other cases")
        }

    }

    private fun isStart(replay: Replay, step: Int): Boolean = !replay.children.isEmpty()


    private fun isEnd(replay: Replay, step: Int): Boolean {
        if (replay.children.isEmpty()) {
            return step >= 1
        } else {
            return step > replay.children.size
        }
    }

    private fun isCharacters(replay: Replay, step: Int): Boolean = replay.children.isEmpty()


    override fun getName(): QName {
        return current!!.qName
    }

    override fun getNamespaceURI(prefix: String?): String {
        return ""
    }


    override fun getAttributeCount(): Int = current!!.attributes.size

    override fun getAttributeLocalName(index: Int): String = current!!.attributes[index].name

    override fun getAttributeValue(index: Int): String = current!!.attributes[index].value


    override fun getLocalName(): String = throw UnsupportedOperationException()

    override fun getNamespaceURI(): String = throw UnsupportedOperationException()

    override fun getAttributeName(index: Int): QName = throw UnsupportedOperationException()


    override fun getPIData(): String = throw UnsupportedOperationException()

    override fun getCharacterEncodingScheme(): String = throw UnsupportedOperationException()

    override fun getEncoding(): String = throw UnsupportedOperationException()


    override fun getAttributePrefix(index: Int): String = throw UnsupportedOperationException()

    override fun getNamespacePrefix(index: Int): String = throw UnsupportedOperationException()

    override fun getTextLength(): Int = throw UnsupportedOperationException()

    override fun getProperty(name: String?): Any = throw UnsupportedOperationException()

    override fun isStandalone(): Boolean = throw UnsupportedOperationException()

    override fun isAttributeSpecified(index: Int): Boolean = throw UnsupportedOperationException()

    override fun getAttributeType(index: Int): String = throw UnsupportedOperationException()

    override fun getEventType(): Int = throw UnsupportedOperationException()

    override fun getVersion(): String = throw UnsupportedOperationException()

    override fun getPITarget(): String = throw UnsupportedOperationException()

    override fun close() = throw UnsupportedOperationException()

    override fun nextTag(): Int = throw UnsupportedOperationException()

    override fun isEndElement(): Boolean = throw UnsupportedOperationException()

    override fun isCharacters(): Boolean = throw UnsupportedOperationException()


    override fun getNamespaceURI(index: Int): String = throw UnsupportedOperationException()


    override fun getLocation(): Location = throw UnsupportedOperationException()


    override fun getNamespaceCount(): Int = throw UnsupportedOperationException()

    override fun getNamespaceContext(): NamespaceContext = throw UnsupportedOperationException()

    override fun getTextCharacters(): CharArray = throw UnsupportedOperationException()

    override fun getTextCharacters(sourceStart: Int, target: CharArray?, targetStart: Int, length: Int): Int = throw UnsupportedOperationException()

    override fun require(type: Int, namespaceURI: String?, localName: String?) = throw UnsupportedOperationException()

    override fun getAttributeNamespace(index: Int): String = throw UnsupportedOperationException()

    override fun getElementText(): String = throw UnsupportedOperationException()

    override fun getAttributeValue(namespaceURI: String?, localName: String?): String = throw UnsupportedOperationException()


    override fun isStartElement(): Boolean = throw UnsupportedOperationException()

    override fun standaloneSet(): Boolean = throw UnsupportedOperationException()

    override fun getPrefix(): String = throw UnsupportedOperationException()

    override fun hasName(): Boolean = throw UnsupportedOperationException()


    override fun isWhiteSpace(): Boolean = throw UnsupportedOperationException()

    override fun hasText(): Boolean = throw UnsupportedOperationException()

    override fun getTextStart(): Int = throw UnsupportedOperationException()

}