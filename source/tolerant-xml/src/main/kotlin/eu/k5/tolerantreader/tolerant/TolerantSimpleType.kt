package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.BindContext
import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent

abstract class TolerantSimpleType(val name: QName, val baseName: QName) : TolerantType() {

    override fun getTypeName(): QName {
        return baseName
    }

    override fun getQualifiedName(): QName {
        return name
    }

    override fun pushedOnStack(): Boolean {
        return false
    }

    override fun readValue(context: BindContext, element: TolerantElement, stream: XMLStreamReader): Any? {
        var text = ""
        var balance = 1
        while (stream.hasNext()) {

            val event = stream.next()
            if (event == XMLEvent.CHARACTERS) {
                text += stream.text
            } else if (event == XMLEvent.END_ELEMENT) {
                balance--
                if (balance == 0) {
                    return parse(text)
                }
            } else if (event == XMLEvent.START_ELEMENT) {
                balance++
            }
        }
        return null
    }


    abstract fun parse(text: String): Any
}