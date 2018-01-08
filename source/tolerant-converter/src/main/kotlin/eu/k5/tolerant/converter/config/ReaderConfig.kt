package eu.k5.tolerant.converter.config

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

enum class NumberParsers {
    US, DE, DETECT;
}

@XmlAccessorType(XmlAccessType.NONE)
class ReaderConfig {

    @XmlAttribute(name = "key")
    var key: String? = null

    @XmlElement(name = "xsd")
    var xsd: String? = null

    @XmlElement(name = "numbers")
    var numberParser: NumberParsers? = null


    @XmlElement(name = "lengthRestriction")
    var enforceLengthRestriction: Boolean? = true

}