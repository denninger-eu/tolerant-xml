package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.ReaderContext
import eu.k5.tolerantreader.reader.BindContext
import javax.xml.namespace.QName

class TolerantSimpleRestriction(name: QName, private val base: TolerantSimpleType) : TolerantSimpleType(name, base.getTypeName()) {

    override fun parse(context: ReaderContext, text: String): Any? {
        // TODO: apply additional restriction
        return base.parse(context, text)
    }

}