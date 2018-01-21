package eu.k5.tolerantreader.transformer

import eu.k5.tolerantreader.tolerant.TolerantTransformer


class Transformers {

    val transformes: MutableList<Transformer> = ArrayList()

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
}