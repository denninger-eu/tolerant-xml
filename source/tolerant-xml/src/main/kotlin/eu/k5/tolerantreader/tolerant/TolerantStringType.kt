package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.xs.XSD_NAMESPACE
import javax.xml.namespace.QName

class TolerantStringType(name: QName) : TolerantSimpleType(name, QName(XSD_NAMESPACE, "string")) {
    override fun parse(text: String): Any {
        return text
    }


}