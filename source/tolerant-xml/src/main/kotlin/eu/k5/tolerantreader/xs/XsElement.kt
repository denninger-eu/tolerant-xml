package eu.k5.tolerantreader.xs

import eu.k5.tolerantreader.XSD_NAMESPACE
import eu.k5.tolerantreader.XsNamed
import javax.xml.bind.annotation.*
import javax.xml.namespace.QName


@XmlAccessorType(XmlAccessType.NONE)
class XsElement : XsNamed() {

    override fun localPartName(): String {
        return localName!!
    }


    @XmlAttribute(name = "name")
    var localName: String? = null

    @XmlAttribute(name = "type")
    var type: QName? = null

    @XmlElement(name = "complexType", namespace = XSD_NAMESPACE)
    var complexType: XsComplexType? = null


    override fun postSchemaMarshall(xsSchema: XsSchema) {
        super.postSchemaMarshall(xsSchema)
        complexType?.postSchemaMarshall(xsSchema)
        complexType?.assignOwningElement(this)
    }

}