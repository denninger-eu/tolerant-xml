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

    @XmlElement(name = "namespaces")
    var namespaces: NamespacePrefixConfig? = null
}


@XmlAccessorType(XmlAccessType.NONE)
class NamespacePrefixConfig {

    @XmlAttribute(name = "fallback")
    var fallback: String = "xs"

    @XmlElement(name = "explicit")
    var explicit: List<Explicit> = ArrayList()

    @XmlElement(name = "pattern")
    var pattern: List<Pattern> = ArrayList()
}

@XmlAccessorType(XmlAccessType.NONE)
class Explicit {

    @XmlAttribute(name = "namespace")
    var namespace: String? = null

    @XmlAttribute(name = "prefix")
    var prefix: String? = null
}

@XmlAccessorType(XmlAccessType.NONE)
class Pattern {
    @XmlAttribute(name = "use")
    var use: String? = null

    @XmlAttribute(name = "extract")
    var extract: String? = null
}