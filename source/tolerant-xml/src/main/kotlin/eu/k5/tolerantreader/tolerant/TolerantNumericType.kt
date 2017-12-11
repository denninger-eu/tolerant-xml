package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.XSD_NAMESPACE
import java.math.BigDecimal
import javax.xml.namespace.QName

class TolerantNumericType(name: QName, type: QName, private val parser: (String) -> Any) : TolerantSimpleType(name, type) {

    override fun parse(text: String): Any = parser(text)

}

val DECIMAL_TYPE = TolerantNumericType(QName(XSD_NAMESPACE, "byte"), QName(XSD_NAMESPACE, "byte")) {
    BigDecimal(it)
}

val BYTE_TYPE = TolerantNumericType(QName(XSD_NAMESPACE, "byte"), QName(XSD_NAMESPACE, "byte")) {

}

/*
byte	A signed 8-bit integer
decimal	A decimal value
int	A signed 32-bit integer
integer	An integer value
long	A signed 64-bit integer
negativeInteger	An integer containing only negative values (..,-2,-1)
nonNegativeInteger	An integer containing only non-negative values (0,1,2,..)
nonPositiveInteger	An integer containing only non-positive values (..,-2,-1,0)
positiveInteger	An integer containing only positive values (1,2,..)
short	A signed 16-bit integer
unsignedLong	An unsigned 64-bit integer
unsignedInt	An unsigned 32-bit integer
unsignedShort	An unsigned 16-bit integer
unsignedByte	An unsigned 8-bit integer
*/