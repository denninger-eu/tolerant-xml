package eu.k5.tolerantreader.xs

import eu.k5.tolerantreader.XSD_NAMESPACE
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator
import javax.xml.bind.annotation.*
import javax.xml.namespace.QName

@XmlRootElement(name = "schema", namespace = XSD_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
class XsSchema {

    @XmlAttribute(name = "targetNamespace")
    var targetNamespace: String? = null

    @XmlElement(name = "include", namespace = XSD_NAMESPACE)
    var includes: List<XsInclude> = ArrayList()

    @XmlElement(name = "import", namespace = XSD_NAMESPACE)
    var imports: List<XsImport> = ArrayList()

    @XmlElement(name = "simpleType", namespace = XSD_NAMESPACE)
    var simpleTypes: List<XsSimpleType> = ArrayList()

    @XmlElement(name = "complexType", namespace = XSD_NAMESPACE)
    var complexTypes: List<XsComplexType> = ArrayList()

    @XmlElement(name = "element", namespace = XSD_NAMESPACE)
    var elements: List<XsElement> = ArrayList()

    @XmlTransient
    var schemaLocation: String? = null


    @XmlTransient
    var registry: XsRegistry? = null

    fun complete() {
        simpleTypes.forEach { it.postSchemaMarshall(this) }
        complexTypes.forEach { it.postSchemaMarshall(this) }
        elements.forEach { it.postSchemaMarshall(this) }
    }

    override fun toString(): String {
        return "XsSchema(targetNamespace=$targetNamespace, imports=$imports, simpleTypes=$simpleTypes, complexTypes=$complexTypes, elements=$elements, schemaLocation=$schemaLocation, registry=$registry)"
    }


}

@XmlAccessorType(XmlAccessType.NONE)
class XsImport {

    @XmlAttribute(name = "schemaLocation")
    var schemaLocation: String? = null

    @XmlAttribute(name = "namespace")
    var namespace: String? = null

    @XmlTransient
    var resolvedSchema: XsSchema? = null

}

@XmlAccessorType(XmlAccessType.NONE)
class XsInclude {
    @XmlAttribute(name = "schemaLocation")
    var schemaLocation: String? = null
}
