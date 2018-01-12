package eu.k5.tolerantreader.tolerant

import com.google.common.collect.ImmutableMap
import eu.k5.tolerantreader.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import javax.swing.text.NumberFormatter
import javax.xml.namespace.QName
import kotlin.collections.HashMap
import kotlin.collections.HashSet


class TolerantBaseTypes(private val initContext: InitContext) {


    private val simpleTypes: Map<QName, TolerantSimpleType>

    init {


        val st = ImmutableMap.builder<QName, TolerantSimpleType>()
        st.put(xsString, TolerantStringType(xsString))
        st.put(xsAnyUri, TolerantStringType(xsAnyUri))
        st.put(xsBase64Binary, TolerantBase64BinaryType())
        st.put(xsHexBinary, TolerantHexBinaryType())
        st.put(xsBoolean, TolerantBooleanType())
        st.put(xsQname, TolerantQNameType())


        st.put(xsDate, TolerantTemporalType(xsDate))
        st.put(xsDatetime, TolerantTemporalType(xsDatetime))
        st.put(xsDuration, TolerantTemporalType(xsDuration))
        st.put(xsGDay, TolerantTemporalType(xsGDay))
        st.put(xsGMonth, TolerantTemporalType(xsGMonth))
        st.put(xsGMonthDay, TolerantTemporalType(xsGMonthDay))
        st.put(xsGYear, TolerantTemporalType(xsGYear))
        st.put(xsGYearMonth, TolerantTemporalType(xsGYearMonth))
        st.put(xsTime, TolerantTemporalType(xsTime))

        st.put(xsId, TolerantIdType())
        st.put(xsIdRef, TolerantIdRefType())
        st.putAll(createNumericTypes(initContext))

        simpleTypes = st.build()
    }


    private fun createNumericTypes(initContext: InitContext): Map<QName, TolerantNumericType> {
        if (NumberFormat.DE_DE.equals(initContext.getNumberFormat())) {
            return createDeDeNumericTypes()
        } else if (NumberFormat.DETECT.equals(initContext.getNumberFormat())) {
            return createDetectNumericTypes()
        } else {
            return createEnUsNumericTypes()
        }
    }


    private fun createEnUsNumericTypes(): Map<QName, TolerantNumericType> {
        val enUsNumeric = ImmutableMap.builder<QName, TolerantNumericType>()
        enUsNumeric.put(xsByte, byteType)
        enUsNumeric.put(xsDecimal, TolerantNumericType(xsDecimal, enUsParser))
        enUsNumeric.put(xsInt, intType)
        enUsNumeric.put(xsInteger, integerType)
        enUsNumeric.put(xsLong, longType)
        enUsNumeric.put(xsNegativeInteger, negativeIntegerType)
        enUsNumeric.put(xsNonNegativeInteger, nonNegativeIntegerType)
        enUsNumeric.put(xsNonPositiveInteger, nonPositiveIntegerType)
        enUsNumeric.put(xsPositiveInteger, positiveIntegerType)
        enUsNumeric.put(xsShort, shortType)
        enUsNumeric.put(xsUnsignedLong, unsignedLongType)
        enUsNumeric.put(xsUnsignedInt, unsignedIntType)
        enUsNumeric.put(xsUnsignedShort, unsignedShortType)
        enUsNumeric.put(xsUnsignedByte, unsignedByteType)
        enUsNumeric.put(xsDouble, doubleType)
        enUsNumeric.put(xsFloat, floatType)
        return enUsNumeric.build()
    }


    private fun createDeDeNumericTypes(): Map<QName, TolerantNumericType> {
        val enUsNumeric = ImmutableMap.builder<QName, TolerantNumericType>()
        enUsNumeric.put(xsByte, byteType)
        enUsNumeric.put(xsDecimal, decimalType)
        enUsNumeric.put(xsInt, intType)
        enUsNumeric.put(xsInteger, integerType)
        enUsNumeric.put(xsLong, longType)
        enUsNumeric.put(xsNegativeInteger, negativeIntegerType)
        enUsNumeric.put(xsNonNegativeInteger, nonNegativeIntegerType)
        enUsNumeric.put(xsNonPositiveInteger, nonPositiveIntegerType)
        enUsNumeric.put(xsPositiveInteger, positiveIntegerType)
        enUsNumeric.put(xsShort, shortType)
        enUsNumeric.put(xsUnsignedLong, unsignedLongType)
        enUsNumeric.put(xsUnsignedInt, unsignedIntType)
        enUsNumeric.put(xsUnsignedShort, unsignedShortType)
        enUsNumeric.put(xsUnsignedByte, unsignedByteType)
        enUsNumeric.put(xsDouble, doubleType)
        enUsNumeric.put(xsFloat, floatType)
        return enUsNumeric.build()
    }


    private fun createDetectNumericTypes(): Map<QName, TolerantNumericType> {
        val detectNumeric = ImmutableMap.builder<QName, TolerantNumericType>()
        detectNumeric.put(xsByte, byteType)
        detectNumeric.put(xsDecimal, decimalType)
        detectNumeric.put(xsInt, intType)
        detectNumeric.put(xsInteger, integerType)
        detectNumeric.put(xsLong, longType)
        detectNumeric.put(xsNegativeInteger, negativeIntegerType)
        detectNumeric.put(xsNonNegativeInteger, nonNegativeIntegerType)
        detectNumeric.put(xsNonPositiveInteger, nonPositiveIntegerType)
        detectNumeric.put(xsPositiveInteger, positiveIntegerType)
        detectNumeric.put(xsShort, shortType)
        detectNumeric.put(xsUnsignedLong, unsignedLongType)
        detectNumeric.put(xsUnsignedInt, unsignedIntType)
        detectNumeric.put(xsUnsignedShort, unsignedShortType)
        detectNumeric.put(xsUnsignedByte, unsignedByteType)
        detectNumeric.put(xsDouble, doubleType)
        detectNumeric.put(xsFloat, floatType)
        return detectNumeric.build()
    }

    fun getBaseType(qName: QName): TolerantSimpleType?
            = simpleTypes[qName]


    companion object {
        private val enUsParser: (String) -> BigDecimal = {
            BigDecimal(it)
        }
        private val deDeParser: (String) -> BigDecimal = {
            BigDecimal(it)
        }
    }
}

class TolerantStringType(name: QName) : TolerantSimpleType(name, xsString) {
    override fun parse(context: BindContext, text: String)
            : Any = text
}

class TolerantBooleanType : TolerantSimpleType(xsBoolean, xsBoolean) {
    override fun parse(context: BindContext, text: String)
            : Any = java.lang.Boolean.valueOf(text.trim())
}

class TolerantQNameType : TolerantSimpleType(xsQname, xsQname) {
    override fun parse(context: BindContext, text: String): Any {
        TODO("Implement")
    }

}


class TolerantBase64BinaryType : TolerantSimpleType(xsBase64Binary, xsBase64Binary) {
    override fun parse(context: BindContext, text: String)
            : Any = Base64.getDecoder().decode(text)

}


class TolerantHexBinaryType : TolerantSimpleType(xsHexBinary, xsHexBinary) {
    override fun parse(context: BindContext, text: String): Any {
        return Base64.getDecoder().decode(text)
    }
}

class TolerantTemporalType(name: QName) : TolerantSimpleType(name, name) {
    override fun parse(context: BindContext, text: String): Any {
        TODO("Implement")
    }
}


class TolerantNumericType(name: QName, private val parser: (String) -> Any) : TolerantSimpleType(name, name) {
    override fun parse(context: BindContext, text: String)
            : Any = parser(text)

    override fun toString()
            : String = getQualifiedName().toString()
}

enum class NumberFormat {
    EN_US, DE_DE, DETECT
}

private val numericTypes = HashMap<NumberFormat, Map<QName, TolerantNumericType>>()


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