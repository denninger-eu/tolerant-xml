package eu.k5.tolerantreader.strict

import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamWriter

class StrictSimpleElementType(val name: QName, private val convert: StrictTypeAdapter) : StrictType() {

    override fun getQualifiedName(): QName = name

    override fun write(context: StrictContext, instance: Any, xmlStreamWriter: XMLStreamWriter) {
        val value = convert.convert(instance)
        xmlStreamWriter.writeCharacters(value)
    }
}