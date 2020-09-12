package eu.k5.tolerantreader.xs

import eu.k5.tolerantreader.XSD_NAMESPACE
import eu.k5.tolerantreader.XsNamed
import eu.k5.tolerantreader.xsString
import javax.xml.bind.annotation.*
import javax.xml.namespace.QName

@XmlAccessorType(XmlAccessType.NONE)
class XsSimpleType : XsNamed() {


    @XmlAttribute(name = "name")
    var name: String? = null

    @XmlElement(namespace = XSD_NAMESPACE, name = "restriction")
    var restriction: XsSimpleTypeRestriction? = null


    override fun postSchemaMarshall(xsSchema: XsSchema) {
        owningSchema = xsSchema
    }


    override fun localPartName(): String {
        return name!!
    }

    fun isEnum(): Boolean {
        if (restriction?.isEnum() == true) {
            return restriction?.base?.equals(xsString) == true
        }
        return false
    }


    fun enumLiterals(): List<String> {
        return restriction?.enumeration.orEmpty()
                .filter { it.value != null }
                .map { it.value!! }
    }
}


@XmlAccessorType(XmlAccessType.NONE)
class XsSimpleTypeRestriction {

    @XmlAttribute(name = "base")
    var base: QName? = null

    @XmlElement(namespace = XSD_NAMESPACE, name = "enumeration")
    var enumeration: List<XsEnumerationRestriction>? = ArrayList()

    fun isEnum(): Boolean = enumeration?.isEmpty()?.not() ?: false

}

@XmlAccessorType(XmlAccessType.NONE)
class XsPatternRestriction {
    @XmlAttribute(name = "value")
    var value: String? = null
}

@XmlAccessorType(XmlAccessType.NONE)
class XsEnumerationRestriction {

    @XmlAttribute(name = "value")
    var value: String? = null

}