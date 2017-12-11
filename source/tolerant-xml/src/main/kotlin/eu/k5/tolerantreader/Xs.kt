package eu.k5.tolerantreader

import eu.k5.tolerantreader.xs.XsSchema
import javax.xml.bind.annotation.XmlTransient
import javax.xml.namespace.QName

const val XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema"

val xsString = QName(XSD_NAMESPACE, "string")
val xsAnyUri = QName(XSD_NAMESPACE, "anyURI")
val xsBase64Binary = QName(XSD_NAMESPACE, "base64Binary")
val xsHexBinary = QName(XSD_NAMESPACE, "hexBinary")
val xsBoolean = QName(XSD_NAMESPACE, "boolean")
val xsQname = QName(XSD_NAMESPACE, "QName")
val xsDate = QName(XSD_NAMESPACE, "date")
val xsDatetime = QName(XSD_NAMESPACE, "dateTime")
val xsDuration = QName(XSD_NAMESPACE, "duration")
val xsGDay = QName(XSD_NAMESPACE, "gDay")
val xsGMonth = QName(XSD_NAMESPACE, "gMonth")
val xsGMonthDay = QName(XSD_NAMESPACE, "gMonthDay")
val xsGYear = QName(XSD_NAMESPACE, "gYear")
val xsGYearMonth = QName(XSD_NAMESPACE, "gYearMonth")
val xsTime = QName(XSD_NAMESPACE, "time")


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