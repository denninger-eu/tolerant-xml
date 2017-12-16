package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.BindContext
import javax.xml.namespace.QName

class TolerantEnumType(name: QName, base: QName) : TolerantSimpleType(name, base) {


    override fun parse(context: BindContext, text: String): Any {

        return text

    }

}