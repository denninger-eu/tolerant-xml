package eu.k5.tolerantreader.xs

import javax.xml.bind.annotation.*
import javax.xml.namespace.QName

@XmlAccessorType(XmlAccessType.NONE)
class XsComplexType : XsNamed() {

    @XmlAttribute(name = "name")
    var localName: String? = null
    @XmlAttribute(name = "abstract")
    var abstract: Boolean? = null

    @XmlElement(name = "attribute", namespace = XSD_NAMESPACE)
    var attributes: List<XsAttribute> = ArrayList()

    @XmlElement(name = "sequence", namespace = XSD_NAMESPACE)
    var sequence: XsSequence? = null

    @XmlElement(name = "complexContent", namespace = XSD_NAMESPACE)
    var complexContent: XsComplexContent? = null


    @XmlTransient
    var owningElement: XsElement? = null

    @XmlTransient
    var concreteSubtypes: MutableList<QName> = ArrayList()


    fun isAbstract(): Boolean = abstract == true

    fun getDeclaredElements(): List<XsComplexElement> {
        val s = sequence
        if (s != null) {
            return s.elements
        }
        val cc = complexContent
        if (cc != null) {
            return cc.getDeclaredElements()
        }
        return ArrayList()
    }

    fun getBaseName(): QName? {
        return complexContent?.extension?.base
    }

    fun getBaseComplexType(): XsComplexType? {
        val baseName = getBaseName()
        if (baseName != null) {
            return owningSchema?.getComplexType(baseName)
        }
        return null
    }

    fun getDeclaredAttributes(): List<XsAttribute> {
        if (attributes.isEmpty()) {
            return complexContent?.extension?.attributes.orEmpty()
        }
        return attributes
    }

    override fun localPartName(): String {
        return localName ?: owningElement?.localName ?: ""
    }

    override fun postSchemaMarshall(xsSchema: XsSchema) {
        super.postSchemaMarshall(xsSchema)

        sequence?.elements?.forEach({ e ->
            e.postSchemaMarshall(xsSchema)
        })
        complexContent?.postSchemaMarshall(xsSchema)
        attributes?.forEach { a -> a.postSchemaMarshall(xsSchema) }
    }

    fun assignOwningElement(element: XsElement) {
        owningElement = element
    }

    private var allElements: List<XsComplexElement>? = null

    fun getAllElememts(): List<XsComplexElement> {
        if (allElements == null) {
            var elements = ArrayList<XsComplexElement>()
            getBaseComplexType()?.getAllElememts()?.forEach { elements.add(it) }
            elements.addAll(getDeclaredElements())
            allElements = elements
        }
        return allElements!!
    }

    private var allAttributes: List<XsAttribute>? = null

    fun getAllAttributes(): List<XsAttribute> {
        if (allAttributes == null) {
            var attributes = ArrayList<XsAttribute>()
            getBaseComplexType()?.getAllAttributes()?.forEach { attributes.add(it) }
            attributes.addAll(getDeclaredAttributes())
            allAttributes = attributes
        }
        return allAttributes!!
    }

    fun addConcreteSubtype(name: QName) {
        concreteSubtypes.add(name)
    }

    fun getAllConcreteSubtypes(): List<QName> {
        return concreteSubtypes
    }

}

@XmlAccessorType(XmlAccessType.NONE)
class XsAttribute {

    @XmlAttribute(name = "name")
    var name: String? = null
    @XmlAttribute(name = "type")
    var type: QName? = null

    @XmlAttribute(name = "type")
    var typeName: String? = null
    @XmlTransient
    var owningSchema: XsSchema? = null


    fun postSchemaMarshall(xsSchema: XsSchema) {
        owningSchema = xsSchema
        if (type == null) {
            type = QName(owningSchema?.targetNamespace, typeName)
        }
    }

}

@XmlAccessorType(XmlAccessType.NONE)
class XsSequence {


    @XmlElement(name = "element", namespace = XSD_NAMESPACE)
    var elements: List<XsComplexElement> = ArrayList()

}

@XmlAccessorType(XmlAccessType.NONE)
class XsComplexContent {

    @XmlElement(name = "extension", namespace = XSD_NAMESPACE)
    var extension: XsComplexExtension? = null

//    var restriction: XsComplexRestriction? = null


    fun getDeclaredElements(): List<XsComplexElement> {
        val e = extension
        if (e != null) {
            return e.getDeclaredElements()
        }
        return ArrayList()
    }

    fun postSchemaMarshall(xsSchema: XsSchema) {
        extension?.postSchemaMarshall(xsSchema)
    }
}


@XmlAccessorType(XmlAccessType.NONE)
class XsSimpleContent {

    var extension: XsComplexExtension? = null

}

@XmlAccessorType(XmlAccessType.NONE)
class XsComplexExtension {

    @XmlAttribute(name = "base")
    var base: QName? = null

    @XmlElement(name = "sequence", namespace = XSD_NAMESPACE)
    var sequence: XsSequence? = null

    @XmlElement(name = "attribute", namespace = XSD_NAMESPACE)
    var attributes: List<XsAttribute> = ArrayList()

    @XmlTransient
    var owningSchema: XsSchema? = null


    fun getDeclaredElements(): List<XsComplexElement> {
        val s = sequence
        if (s != null) {
            return s.elements
        }
        return ArrayList()
    }

    fun postSchemaMarshall(xsSchema: XsSchema) {
        owningSchema = xsSchema
        sequence?.elements?.forEach { it.postSchemaMarshall(xsSchema) }
    }
}

class XsComplexRestriction {

}

@XmlAccessorType(XmlAccessType.NONE)
class XsComplexElement : XsNamed() {

    @XmlAttribute(name = "name")
    var name: String? = null

    @XmlAttribute(name = "type")
    var type: QName? = null

    @XmlAttribute(name = "type")
    var typeName: String? = null

    @XmlAttribute(name = "minOccurs")
    var minOccurs: String? = "0"

    @XmlAttribute(name = "maxOccurs")
    var maxOccurs: String? = "1"

    fun getTypeName(): QName {
        if (type != null) {
            return type!!
        } else {
            return QName(owningSchema!!.targetNamespace, typeName)
        }
    }


    fun isLocalType(): Boolean {
        return type == null
    }

    override fun localPartName(): String {
        return name!!
    }

    fun isList(): Boolean {
        return !"0".equals(minOccurs) || !"1".equals(maxOccurs)
    }
}
