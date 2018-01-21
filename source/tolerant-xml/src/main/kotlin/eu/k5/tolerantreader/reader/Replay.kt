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

    fun asStreamReader(targetName: String): XMLStreamReader {
        return ReplayStream(this, targetName, childrenDepth)
    }

    companion object {
        fun record(qName: QName, stream: XMLStreamReader): Replay {

            var root = Replay(qName, null, readAttributes(stream))

            var current = root


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
                        current.parent!!.children.add(current)
                        current = current.parent!!
                        current.childrenDepth++
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
        private val replay: Replay,
        private val targetName: String,
        childrenDepth: Int
) : XMLStreamReader {

    private var current: Replay? = replay

    private var level = 0

    private var index = Array(childrenDepth) { -1 }

    init {
        if (childrenDepth > 0) {
            index[0]--
        }
    }

    override fun hasNext(): Boolean {
        if (level < 0) return false
        return index[level] < 2
    }

    override fun getText(): String? {
        return current!!.replayText
    }

    override fun next(): Int {
        index[level]++
        if (index[level] == -1) {
            index[level]++
            return XMLEvent.START_ELEMENT
        } else if (index[level] > (current!!.children.size)) {
            level--
            current = current!!.parent
            return XMLEvent.END_ELEMENT
        } else {
            if (!current!!.children.isEmpty()) {
                current = current!!.children[index[level] - 1]
                level++
                return XMLEvent.START_ELEMENT
            } else {
                return XMLEvent.CHARACTERS
            }

        }
    }

    override fun getName(): QName {
        if (level == 0) {
            return QName(replay.qName.namespaceURI, targetName)
        } else {
            return current!!.qName
        }
    }


    // Unused
    override fun getLocalName(): String {
        return targetName
    }

    // Unused
    override fun getNamespaceURI(): String {
        return replay.qName.namespaceURI
    }

    override fun getAttributeCount(): Int = current!!.attributes.size

    override fun getAttributeLocalName(index: Int): String = current!!.attributes[index].name

    override fun getAttributeValue(index: Int): String = current!!.attributes[index].value

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


    override fun getNamespaceURI(prefix: String?): String = throw UnsupportedOperationException()

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