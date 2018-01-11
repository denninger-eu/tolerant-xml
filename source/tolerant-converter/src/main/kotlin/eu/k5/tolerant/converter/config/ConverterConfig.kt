package eu.k5.tolerant.converter.config

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.NONE)
class ConverterConfig {

    @XmlAttribute(name = "key")
    var key: String? = null

    @XmlElement(name = "name")
    var name: String? = null

    @XmlElement(name = "readerRef")
    var readerRef: String? = null

    @XmlElement(name = "writerRef")
    var writerRef: String? = null
}