package eu.k5.tolerantreader.strict

import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamWriter

class StrictElement(val name: QName, val complexType: StrictComplexType) : StrictType() {


    override fun write(instance: Any, xmlStramWriter: XMLStreamWriter) {
        xmlStramWriter.writeStartElement(name.localPart)

        complexType.write(instance, xmlStramWriter)

        xmlStramWriter.writeEndElement()

    }

}