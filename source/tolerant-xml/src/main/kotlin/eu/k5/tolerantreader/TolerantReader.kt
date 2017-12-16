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

    INVALID_ENUM_LITERAL
}

class BindContext(private val schema: TolerantSchema, private val root: RootElement) {

    private val elements: Deque<TolerantElement> = ArrayDeque()
    private val types: Deque<TolerantType> = ArrayDeque()
    private val instances: Deque<Any> = ArrayDeque()

    private val openReferences: MutableList<OpenReference> = ArrayList()

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

    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

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
        elements.pop()
        instances.pop()
        types.pop()
    }

    fun getCurrent(): TolerantElement? {
        if (elements.isEmpty()) {
            return null
        }
        return elements.peek()
    }

    fun getCurrentInstance(): Any {
        return instances.peek()
    }

    fun sealedRoot(): Any = root.seal()
    fun getComplexType(name: QName): TolerantType {
        return schema.getComplexType(name)
    }

    fun addViolation(type: Violation, description: String) {
        logger.warn("{}: {}", type, description)
    }

    companion object {
        val logger = LoggerFactory.getLogger(BindContext.javaClass)
    }

    private val entities: MutableMap<String, Any> = HashMap()

    fun getEntityById(id: String): Any? {
        return entities[id]
    }

    fun addOpenIdRef(id: String, instance: Any, delegate: Assigner) {
        openReferences.add(OpenReference(id, instance, delegate))
    }

    fun registerEntity(value: String, instance: Any) {
        entities.put(value, instance)
    }
}

class OpenReference(val id: String, val instance: Any, val assigner: Assigner) {

}


class TolerantReader(val schema: TolerantSchema) {


    fun read(stream: XMLStreamReader): Any? {

        val context = schema.createContext()

        while (stream.hasNext()) {
            val event = stream.next()

            if (XMLEvent.START_ELEMENT == event) {

                val localName = stream.localName
                val namespaceURI: String? = stream.namespaceURI
                val qname = QName(namespaceURI, localName)

                val element =
                        if (context.isEmpty()) {
                            schema.getElement(namespaceURI, localName)
                        } else {
                            context.getElement(namespaceURI, localName)
                        }

                if (element == null) {
                    // balance stream
                    context.addViolation(Violation.UNKNOWN_CONTENT, "Element: " + qname)
                    skipToEndElement(stream)
                    continue
                }


                val type = element.type.asSubtype(context, stream)
                val readValue = type.readValue(context, element, stream)
                if (readValue != null) {

                    element.assigner.assign(context, context.getCurrentInstance(), readValue)

                    if (type.pushedOnStack()) {
                        context.push(element, readValue, type)
                    }
                }

            } else if (XMLEvent.END_ELEMENT == event) {
                context.pop()
            }
        }


        context.postProcess()



        return context.sealedRoot()

    }

    private fun skipToEndElement(stream: XMLStreamReader) {
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