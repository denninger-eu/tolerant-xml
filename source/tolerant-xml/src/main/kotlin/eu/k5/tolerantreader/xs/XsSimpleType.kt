package eu.k5.tolerantreader.xs

import javax.xml.bind.annotation.*
import javax.xml.namespace.QName

@XmlAccessorType(XmlAccessType.NONE)
class XsSimpleType : XsNamed() {


    @XmlAttribute(name = "name")
    var name: String? = null

    @XmlElement(name = "restriction")
    var restriction: XsSimpleTypeRestriction? = null


    override fun postSchemaMarshall(xsSchema: XsSchema) {
        owningSchema = xsSchema
    }



    override fun localPartName(): String {
        return name!!
    }

}

@XmlAccessorType(XmlAccessType.NONE)
class XsSimpleTypeRestriction {

    @XmlAttribute(name = "base")
    var base: QName? = null

    @XmlElement(name = "enumeration")
    var enumeration: List<XsEnumerationRestriction> = ArrayList()

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