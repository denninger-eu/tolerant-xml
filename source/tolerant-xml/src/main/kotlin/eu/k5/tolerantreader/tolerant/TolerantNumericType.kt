package eu.k5.tolerantreader.tolerant

import java.math.BigDecimal
import javax.xml.namespace.QName

class TolerantNumericType(name: QName, type: QName) : TolerantSimpleType(name, type) {


    override fun parse(text: String): Any {
        return BigDecimal(text)
    }

}