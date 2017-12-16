package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.BindContext
import javax.xml.namespace.QName

class TolerantEnumType(name: QName, private val literalFactory: (BindContext, String) -> Any?) : TolerantSimpleType(name, name) {


    override fun parse(context: BindContext, text: String): Any? {
        return literalFactory(context, text)
    }

}