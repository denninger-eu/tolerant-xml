package model.complex


open class BaseType {

    var baseElement: String? = null

    var baseAttribute: String? = null
}

class SubType : BaseType() {

    var subElement: String? = null

    var subAttribute: String? = null

}