package eu.k5.tolerant.converter.config

import eu.k5.tolerantreader.binding.dom.ConfigurableNamespaceStrategy
import eu.k5.tolerantreader.binding.dom.NamespaceStrategy
import eu.k5.tolerantreader.binding.dom.NamespaceStrategyConfiguration
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.NONE)
class WriterConfig {

    @XmlAttribute(name = "key")
    var key: String? = null

    @XmlElement(name = "fallbackPrefix")
    var fallbackPrefix: String = "ns"

    @XmlElement(name = "explicitPrefix")
    var explicitPrefix: List<Explicit>? = null

    @XmlElement(name = "patternPrefix")
    var patternPrefix: List<Pattern>? = null

    fun createNamespaceStrategy(): NamespaceStrategy {
        val nsConfig = NamespaceStrategyConfiguration(fallbackPrefix)
        for (prefix in explicitPrefix.orEmpty()) {
            nsConfig.explicit.put(prefix.namespace!!, prefix.prefix!!)
        }
        for (pattern in patternPrefix.orEmpty()) {
            nsConfig.addPattern(pattern.use!!, pattern.extract!!)
        }
        return ConfigurableNamespaceStrategy(nsConfig)

    }

}


@XmlAccessorType(XmlAccessType.NONE)
class Explicit(
        namespace: String?,
        prefix: String?
) {

    constructor() : this(null, null)

    @XmlAttribute(name = "namespace")
    var namespace: String? = namespace

    @XmlAttribute(name = "prefix")
    var prefix: String? = prefix
}

@XmlAccessorType(XmlAccessType.NONE)
class Pattern(
        use: String?,
        extract: String?
) {
    constructor() : this(null, null)

    @XmlAttribute(name = "use")
    var use: String? = use

    @XmlAttribute(name = "extract")
    var extract: String? = extract
}