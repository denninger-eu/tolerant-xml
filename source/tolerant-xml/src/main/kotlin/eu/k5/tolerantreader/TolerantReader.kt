package eu.k5.tolerantreader

import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.tolerant.*
import org.slf4j.LoggerFactory
import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

enum class Violation {
    TYPE_MISMATCH,

    UNKNOWN_CONTENT,

    MISSING_ENTITY,

    INVALID_ENUM_LITERAL,

}

class BindContext(
        private val schema: TolerantSchema,
        private val root: RootElement,
        val readerConfig: TolerantReaderConfiguration
) {

    private val trackComments: Boolean = true
    private val elements: Deque<TolerantElement> = ArrayDeque()
    private val types: Deque<TolerantType> = ArrayDeque()
    private val instances: Deque<Any> = ArrayDeque()
    private val openReferences: MutableList<OpenReference> = ArrayList()
    private val comments: MutableList<String> = ArrayList()

    init {
        instances.push(root)
    }

    fun postProcess() {
        for (open in openReferences) {
            val entity = getEntityById(open.id)
            if (entity != null) {
                open.assigner.assign(this, open.instance, entity)
            } else {
                addViolation(Violation.MISSING_ENTITY, "")
            }

        }
    }

    fun isEmpty(): Boolean
            = elements.isEmpty()


    fun getElement(namespaceURI: String?, localName: String?): TolerantElement? {
        val current = types.peek()
        if (current is TolerantComplexType) {
            return current.getElement(namespaceURI, localName)
        } else if (current is TolerantComplexProxy) {
            return current.getElement(namespaceURI, localName)
        }
        return null
    }

    fun push(element: TolerantElement, instance: Any, type: TolerantType) {
        elements.push(element)
        types.push(type)
        instances.push(instance)
    }

    fun pop() {
        val element = elements.pop()
        val instance = instances.pop()
        val type = types.pop()

        type.closeType(this, instance)
    }


    fun getCurrentInstance(): Any?
            = instances.peek()


    fun sealedRoot(): Any?
            = root.seal(this)

    fun getComplexType(name: QName): TolerantType {
        return schema.getComplexType(name)
    }

    fun addViolation(type: Violation, description: String) {
        logger.warn("{}: {}", type, description)
    }

    companion object {
        val logger = LoggerFactory.getLogger(BindContext::class.java)
    }

    private val entities: MutableMap<String, Any> = HashMap()

    fun getEntityById(id: String): Any?
            = entities[id]


    fun addOpenIdRef(id: String, instance: Any, delegate: Assigner) {
        openReferences.add(OpenReference(id, instance, delegate))
    }

    fun registerEntity(value: String, instance: Any) {
        entities.put(value, instance)
    }

    fun addComment(comment: String) {
        if (trackComments) {
            comments.add(comment)
        }
    }

    fun retrieveComments(): List<String> {
        if (!trackComments) {
            return comments
        }
        val retrieved = ArrayList(comments)
        comments.clear()
        return retrieved
    }


    fun pushFrameElement(stream: XMLStreamReader) {
        root.pushFrameElement(this, stream)
    }

    fun keepFrame(): Boolean {
        return true;
    }

    fun popFrame() {
        root.popFrameElement(this)
    }

    fun keepCharacters(): Boolean
            = keepFrame() && isEmpty()

    fun addCharacters(elementText: String) {
        root.addCharacters(elementText)
    }

}

class OpenReference(
        val id: String,
        val instance: Any,
        val assigner: Assigner
)

class TolerantReaderConfiguration(init: Map<Class<*>, Any>) {
    private val configs: Map<Class<*>, Any> = Collections.unmodifiableMap(HashMap(init))

    fun <T> queryConfigOrDefault(type: Class<T>, createDefault: () -> T): T {
        val obj = configs[type]
        if (type.isInstance(obj)) {
            return type.cast(obj)
        }
        LOGGER.debug("Accessing not existing config: " + type)
        return createDefault()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TolerantReader::class.java)
    }

}

class TolerantReader(val schema: TolerantSchema) {


    fun read(stream: XMLStreamReader, readerConfig: TolerantReaderConfiguration): Any? {

        val context = schema.createContext(readerConfig)

        while (stream.hasNext()) {
            val event = stream.next()

            if (XMLEvent.START_ELEMENT == event) {

                val localName = stream.localName
                val namespaceURI: String = stream.namespaceURI ?: ""
                val qName = QName(namespaceURI, localName)

                val element =
                        if (context.isEmpty()) {
                            schema.getElement(namespaceURI, localName)
                        } else {
                            context.getElement(namespaceURI, localName)
                        }

                if (element == null) {
                    // balance stream
                    if (context.keepFrame()) {

                        context.pushFrameElement(stream)
                    } else {
                        context.addViolation(Violation.UNKNOWN_CONTENT, "Element: " + qName)
                        skipToEndElement(context, stream)
                    }
                    continue
                }


                val type = element.type.asSubtype(context, stream)
                val readValue = type.readValue(context, element, stream)
                if (readValue != null) {

                    element.assigner.assign(context, context.getCurrentInstance()!!, readValue)

                    if (type.pushedOnStack()) {
                        context.push(element, readValue, type)
                    }
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
                    context.pop()

                }
            }
        }
        context.postProcess()
        return context.sealedRoot()
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