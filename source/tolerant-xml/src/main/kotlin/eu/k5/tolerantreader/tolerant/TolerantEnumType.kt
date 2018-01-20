package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.ReaderContext
import eu.k5.tolerantreader.reader.BindContext
import eu.k5.tolerantreader.binding.EnumSupplier
import javax.xml.namespace.QName

class TolerantEnumType(name: QName, private val enumSupplier: EnumSupplier) : TolerantSimpleType(name, enumSupplier.targetType) {


    override fun parse(context: ReaderContext, text: String): Any? {
        return enumSupplier.parser(context, text)
    }

}