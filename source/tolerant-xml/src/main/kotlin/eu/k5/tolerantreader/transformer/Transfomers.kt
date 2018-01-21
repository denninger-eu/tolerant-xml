package eu.k5.tolerantreader.transformer

import javax.xml.namespace.QName


class Transformers {

    val transformers: MutableList<Transformer> = ArrayList()

}


class Transformer() {

    var type: String? = null

    var target: String? = null

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