package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.binding.EnumSupplier
import eu.k5.tolerantreader.xsString
import javax.xml.namespace.QName

class TolerantEnumType(name: QName, private val enumSupplier: EnumSupplier) : TolerantSimpleType(name, enumSupplier.targetType) {


    override fun parse(context: BindContext, text: String): Any? {
        return enumSupplier.parser(context, text)
    }

}