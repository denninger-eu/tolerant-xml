package eu.k5.tolerantreader.strict

import eu.k5.tolerantreader.Reader
import javax.xml.stream.XMLStreamWriter


class StrictComplexType(val type: Class<*>, private val attributes: List<StrictAttribute>, private val elements: List<StrictComplexElement>) : StrictType() {


    override fun write(instance: Any, xmlStreamWriter: XMLStreamWriter) {
        for (attribute in attributes) {
            attribute.write(instance, xmlStreamWriter)
        }

        for (element in elements) {
            element.write(instance, xmlStreamWriter)
        }

    }
}

class StrictComplexProxy() : StrictType() {

    var delegate: StrictComplexType? = null

    override fun write(instance: Any, xmlStreamWriter: XMLStreamWriter) {
        delegate?.write(instance, xmlStreamWriter)
    }
}

class StrictAttribute(val name: String, private val reader: Reader, private val adapter: StrictTypeAdapter) {

    fun write(instance: Any, xmlStreamWriter: XMLStreamWriter) {
        val attributeValue = reader.read(instance)
        if (attributeValue != null) {
            val stringValue = adapter.convert(attributeValue)
            xmlStreamWriter.writeAttribute(name, stringValue)
        }
    }
}

class StrictComplexElement(val name: String, val reader: Reader, val type: StrictType) {
    fun write(instance: Any, xmlStreamWriter: XMLStreamWriter) {
        val elementValue = reader.read(instance)
        if (elementValue != null) {
            xmlStreamWriter.writeStartElement(name)
            type.write(elementValue, xmlStreamWriter)
            xmlStreamWriter.writeEndElement()
        }
    }
}

