package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.*
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