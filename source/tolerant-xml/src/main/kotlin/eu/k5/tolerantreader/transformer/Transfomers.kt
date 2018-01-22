package eu.k5.tolerantreader.transformer

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.namespace.QName


@XmlAccessorType(XmlAccessType.NONE)
class Transformers() {

    @XmlAttribute(name = "key")
    var key: String? = null

    @XmlElement(name = "transformer")
    var transformers: MutableList<Transformer> = ArrayList()

    constructor(transformers: List<Transformer>) : this() {
        this.transformers.addAll(transformers)
    }

}


@XmlAccessorType(XmlAccessType.NONE)
class Transformer() {

    @XmlAttribute(name = "type")
    var type: String? = null

    @XmlAttribute(name = "target")
    var target: String? = null

    @XmlAttribute(name = "element")
    var element: String? = null

    constructor(type: String, target: String, element: String) : this() {
        this.type = type
        this.target = target
        this.element = element
    }

    fun getTargetPath(): Array<QName> {
        val targets = target!!.split("/")

        return Array(targets.size) { QName("", targets[it]) }
    }
}