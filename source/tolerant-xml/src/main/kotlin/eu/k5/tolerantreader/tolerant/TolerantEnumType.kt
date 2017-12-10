package eu.k5.tolerantreader.tolerant

import javax.xml.namespace.QName

class TolerantEnumType(name: QName, base: QName) : TolerantSimpleType(name, base) {


    override fun parse(text: String): Any {

        return text

    }

}