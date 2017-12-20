package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.xs.XsComplexType
import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamReader

class TolerantComplexType(private val name: QName,
                          private val konstructor: (elemantName: QName) -> Any,
                          private val elements: Map<String, TolerantElement>,
                          private val concreteSubtypes: TolerantMap<QName>,
                          private val simpleContent: TolerantSimpleContent?) : TolerantType() {

    override fun asSubtype(context: BindContext, stream: XMLStreamReader): TolerantType {
        if (concreteSubtypes.isEmpty()) {
            return this
        }
        val xsiValue = attributeValue(XSI_TYPE, stream)
        if (xsiValue.isEmpty()) {
            return this
        }
        val subtypeName =
                if (!xsiValue.contains(NAMESPACE_SEPARATOR)) {
                    concreteSubtypes.getByLocalName(xsiValue)
                } else {
                    val valueParts = xsiValue.split(NAMESPACE_SEPARATOR)
                    val namespaceURI = stream.getNamespaceURI(valueParts[0])
                    concreteSubtypes.get(QName(namespaceURI, valueParts[1]))
                }

        if (subtypeName != null) {
            val complexType = context.getComplexType(subtypeName)
            if (complexType != null) {
                return complexType
            }
        }
        throw RuntimeException("unable to create subtype")
    }

    private fun attributeValue(name: QName, stream: XMLStreamReader): String {
        for (index in 0..stream.attributeCount) {
            if (stream.getAttributeLocalName(index) == name.localPart) {
                return stream.getAttributeValue(index)
            }
        }
        return ""
    }

    override fun getQualifiedName(): QName = name

    override fun readValue(context: BindContext, element: TolerantElement, stream: XMLStreamReader): Any {
        val newInstance = konstructor(element.qname)
        handleAttributes(context, stream, newInstance)

        simpleContent?.handle(context, element, stream, newInstance)
        return newInstance
    }

    private fun handleAttributes(context: BindContext, stream: XMLStreamReader, instance: Any) {
        for (index in 0 until stream.attributeCount) {
            val localName = stream.getAttributeLocalName(index)
            val attributeElement = elements.get(localName)
            val type = attributeElement?.type
            if (type is TolerantSimpleType) {
                val value = type.parse(context, stream.getAttributeValue(index))
                attributeElement?.assigner?.assign(context, instance, value)
            }
        }
    }


    override fun pushedOnStack(): Boolean = simpleContent == null

    fun getElement(namespaceURI: String?, localName: String?): TolerantElement? {
        return elements.get(localName)
    }
}


/**
 * Mutable delegate to break object graph cycles
 */
class TolerantComplexProxy(val name: QName) : TolerantType() {
    var delegate: TolerantComplexType? = null

    override fun getQualifiedName(): QName = name

    override fun pushedOnStack(): Boolean = true

    override fun readValue(context: BindContext, element: TolerantElement, stream: XMLStreamReader): Any = delegate!!.readValue(context, element, stream)

    override fun asSubtype(context: BindContext, stream: XMLStreamReader): TolerantType = delegate!!.asSubtype(context, stream)

    fun getElement(namespaceURI: String?, localName: String?): TolerantElement? = delegate?.getElement(namespaceURI, localName)

}

class TolerantSimpleContent(val base: TolerantSimpleType, val baseAssigner: Assigner) {

    fun handle(context: BindContext, element: TolerantElement, stream: XMLStreamReader, instance: Any) {
        val baseValue = base.readValue(context, element, stream)
        baseAssigner.assign(context, instance, baseValue)
    }
}