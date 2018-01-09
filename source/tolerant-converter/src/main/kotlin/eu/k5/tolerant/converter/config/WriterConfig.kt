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
    val key: String? = null

    @XmlElement(name = "fallbackPrefix")
    var fallbackPrefix: String = "ns"

    @XmlElement(name = "explicitPrefix")
    val explicitPrefix: List<Explicit>? = null

    @XmlElement(name = "patternPrefix")
    val patternPrefix: List<Pattern>? = null

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