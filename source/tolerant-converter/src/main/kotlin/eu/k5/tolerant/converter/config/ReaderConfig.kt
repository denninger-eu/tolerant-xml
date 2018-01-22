package eu.k5.tolerant.converter.config

import eu.k5.tolerantreader.transformer.Transformer
import javax.xml.bind.annotation.*

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

    @XmlElement(name = "transformerRef")
    var transformerRef: List<String>? = ArrayList()

    @XmlTransient
    val allTransformers: MutableList<Transformer> = ArrayList()

}

