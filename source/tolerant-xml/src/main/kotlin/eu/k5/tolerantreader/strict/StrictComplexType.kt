package eu.k5.tolerantreader.strict

import eu.k5.tolerantreader.Reader
import eu.k5.tolerantreader.XSD_NAMESPACE
import eu.k5.tolerantreader.tolerant.XSI_NAMESPACE
import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamWriter


class StrictComplexType(
        val type: Class<*>,
        private val qName: QName,
        private val attributes: List<StrictAttribute>,
        private val elements: List<StrictComplexElement>
) : StrictType() {
    override fun getQualifiedName(): QName = qName

    val concreteSubtypes: Map<Class<*>, StrictComplexType> = HashMap()

    fun writeAsSubtype(context: StrictContext, instance: Any, xmlStreamWriter: XMLStreamWriter) {

        xmlStreamWriter.writeAttribute(XSI_NAMESPACE, "type", qName.localPart)

        for (attribute in attributes) {
            attribute.write(context, instance, xmlStreamWriter)
        }

        for (element in elements) {
            element.write(context, instance, xmlStreamWriter)
        }
    }

    override fun write(context: StrictContext, instance: Any, xmlStreamWriter: XMLStreamWriter) {
        if (type != instance?.javaClass) {

            val subtype = context.resolveType(instance.javaClass)

            if (subtype != null) {

                val xsiPrefix = context.getNamespacePrefix(XSI_NAMESPACE)

                val typePrefix = context.getNamespacePrefix(subtype.getQualifiedName().namespaceURI)


                xmlStreamWriter.writeAttribute(xsiPrefix, XSI_NAMESPACE, "type", typePrefix + ":" + subtype.getQualifiedName().localPart)

                subtype.write(context, instance, xmlStreamWriter)
            }


        } else {

            for (attribute in attributes) {
                attribute.write(context, instance, xmlStreamWriter)
            }

            for (element in elements) {
                element.write(context, instance, xmlStreamWriter)
            }
        }
    }
}

class StrictComplexProxy(private val qName: QName) : StrictType() {
    var delegate: StrictComplexType? = null

    override fun getQualifiedName(): QName = qName

    override fun write(context: StrictContext, instance: Any, xmlStreamWriter: XMLStreamWriter) {
        delegate?.write(context, instance, xmlStreamWriter)
    }
}

class StrictAttribute(private val qName: QName, private val reader: Reader, private val adapter: StrictTypeAdapter) {

    fun write(context: StrictContext, instance: Any, xmlStreamWriter: XMLStreamWriter) {
        val attributeValue = reader.read(instance)
        if (attributeValue != null) {
            val stringValue = adapter.convert(attributeValue)

            xmlStreamWriter.writeAttribute(context.getNamespacePrefix(qName.namespaceURI), qName.namespaceURI, qName.localPart, stringValue)
        }
    }
}

class StrictComplexElement(val qName: QName, val reader: Reader, val type: StrictType, val expectedType: Class<*>) {
    fun write(context: StrictContext, instance: Any, xmlStreamWriter: XMLStreamWriter) {
        val elementValue = reader.read(instance)
        if (elementValue != null) {


            xmlStreamWriter.writeStartElement(context.getNamespacePrefix(qName.namespaceURI), qName.localPart, qName.namespaceURI)
            type.write(context, elementValue, xmlStreamWriter)
            xmlStreamWriter.writeEndElement()
        }
    }
}

