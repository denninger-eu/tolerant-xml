package eu.k5.tolerantreader.transformer

open class Transformer {

    var typeName: String? = null

}

class AdditionalNameTransformer : Transformer() {

    var additionalName: String? = null

}

class PushTransfomer : Transformer() {

    var into: String? = null

    var intoElement: String? = null


}