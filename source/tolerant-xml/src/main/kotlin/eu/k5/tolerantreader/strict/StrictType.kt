package eu.k5.tolerantreader.strict

import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamWriter

abstract class StrictType {

    abstract fun getQualifiedName(): QName

    abstract fun write(context: StrictContext, instance: Any, xmlStreamWriter: XMLStreamWriter)

}