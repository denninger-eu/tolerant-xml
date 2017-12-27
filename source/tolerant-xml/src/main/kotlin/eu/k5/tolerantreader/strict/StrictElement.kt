package eu.k5.tolerantreader.strict

import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamWriter

class StrictElement(val name: QName, val complexType: StrictComplexType) : StrictType() {
    override fun getQualifiedName(): QName = name


    override fun write(context: StrictContext, instance: Any, xmlStream: XMLStreamWriter) {
        xmlStream.writeStartElement(name.localPart)

        for ((namespace, prefix) in context.getAllNamespaces()) {
            xmlStream.writeNamespace(prefix, namespace)
        }
        complexType.write(context, instance, xmlStream)

        xmlStream.writeEndElement()

    }

}