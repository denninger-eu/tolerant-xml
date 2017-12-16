package eu.k5.tolerantreader.xs

import eu.k5.tolerantreader.XSD_NAMESPACE
import javax.xml.bind.annotation.*
import javax.xml.namespace.QName

@XmlRootElement(name = "schema", namespace = XSD_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
class XsSchema {

    @XmlAttribute(name = "targetNamespace")
    var targetNamespace: String? = null


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
        for (simpleType in simpleTypes) {
            simpleType.postSchemaMarshall(this)
        }
        complexTypes.forEach({ c ->
            c.postSchemaMarshall(this)
        })
        elements.forEach({ e -> e.postSchemaMarshall(this) })


    }

    fun resolveNamespace(prefix: String): String {
        return ""
    }

    fun getComplexType(baseName: QName): XsComplexType? {
        return registry?.getComplexType(baseName.localPart)

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