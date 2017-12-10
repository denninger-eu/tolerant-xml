package eu.k5.tolerantreader.xs

import javax.xml.bind.annotation.XmlTransient
import javax.xml.namespace.QName

const val XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema"


abstract class XsNamed {

    @XmlTransient
    var owningSchema: XsSchema? = null


    abstract fun localPartName(): String

    fun getQualifiedName(): QName {
        val namespace = owningSchema?.targetNamespace ?: ""
        var localPart = localPartName()

        return QName(namespace, localPart)
    }

    open fun postSchemaMarshall(schema: XsSchema) {
        owningSchema = schema
    }



}