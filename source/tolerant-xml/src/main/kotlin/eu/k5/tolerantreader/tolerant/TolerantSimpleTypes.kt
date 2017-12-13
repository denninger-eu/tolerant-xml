package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import javax.xml.namespace.QName

class TolerantStringType(name: QName) : TolerantSimpleType(name, xsString) {
    override fun parse(text: String): Any = text
}

class TolerantBooleanType : TolerantSimpleType(xsBoolean, xsBoolean) {
    override fun parse(text: String): Any = java.lang.Boolean.valueOf(text.trim())
}

class TolerantQNameType : TolerantSimpleType(xsQname, xsQname) {
    override fun parse(text: String): Any {
        TODO("Implement")
    }

}


class TolerantBase64BinaryType : TolerantSimpleType(xsBase64Binary, xsBase64Binary) {
    override fun parse(text: String): Any {
        return Base64.getDecoder().decode(text)
    }
}


class TolerantHexBinaryType : TolerantSimpleType(xsHexBinary, xsHexBinary) {
    override fun parse(text: String): Any {
        return Base64.getDecoder().decode(text)
    }
}

class TolerantTemporalType(name: QName) : TolerantSimpleType(name, name) {
    override fun parse(text: String): Any {
        TODO("Implement")
    }
}


class TolerantNumericType(name: QName, private val parser: (String) -> Any) : TolerantSimpleType(name, name) {
    override fun parse(text: String): Any = parser(text)

    override fun toString(): String = name.toString()
}

val decimalType = TolerantNumericType(xsDecimal) {
    BigDecimal(it)
}

val byteType = TolerantNumericType(xsByte) {
    java.lang.Byte.valueOf(it)
}

val intType = TolerantNumericType(xsInt) {
    java.lang.Integer.valueOf(it)
}


val integerType = TolerantNumericType(xsInteger) {
    BigInteger(it)
}


val longType = TolerantNumericType(xsLong) {
    java.lang.Long.valueOf(it)
}

val negativeIntegerType = TolerantNumericType(xsNegativeInteger) {
    BigInteger(it)
}

val nonNegativeIntegerType = TolerantNumericType(xsNonNegativeInteger) {
    BigInteger(it)
}
val nonPositiveIntegerType = TolerantNumericType(xsNonPositiveInteger) {
    BigInteger(it)
}
val positiveIntegerType = TolerantNumericType(xsPositiveInteger) {
    BigInteger(it)
}
val shortType = TolerantNumericType(xsShort) {
    java.lang.Short.valueOf(it)
}

val unsignedLongType = TolerantNumericType(xsUnsignedLong) {
    BigInteger(it)
}
val unsignedIntType = TolerantNumericType(xsUnsignedInt) {
    java.lang.Long.valueOf(it)
}
val unsignedShortType = TolerantNumericType(xsUnsignedShort) {
    java.lang.Integer.valueOf(it)
}
val unsignedByteType = TolerantNumericType(xsUnsignedByte) {
    java.lang.Short.valueOf(it)
}
val doubleType = TolerantNumericType(xsDouble) {
    java.lang.Double.valueOf(it)
}
val floatType = TolerantNumericType(xsFloat) {
    java.lang.Float.valueOf(it)
}