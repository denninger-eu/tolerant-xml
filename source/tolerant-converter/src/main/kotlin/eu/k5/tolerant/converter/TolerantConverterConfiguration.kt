package eu.k5.tolerant.converter

import javax.xml.bind.annotation.*


@XmlRootElement(name = "ConverterConfig")
@XmlAccessorType(XmlAccessType.NONE)
class TolerantConverterConfiguration {

    @XmlAttribute(name = "key")
    var key: String? = null

    @XmlElement(name = "name")
    var name: String? = null

    @XmlElement(name = "xsd")
    var xsd: String? = null

}