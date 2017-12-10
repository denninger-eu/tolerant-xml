package eu.k5.tolerantreader.strict

import javax.xml.stream.XMLStreamWriter

abstract class StrictType {
    abstract fun write(instance:Any, xmlStreamWriter: XMLStreamWriter)

}