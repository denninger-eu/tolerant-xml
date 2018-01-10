package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.BindContext
import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamReader


abstract class TolerantType() {

    abstract fun getQualifiedName(): QName

    abstract fun readValue(context: BindContext, elementName: TolerantElement, stream: XMLStreamReader): Any?

    abstract fun pushedOnStack(): Boolean

    open fun asSubtype(context: BindContext, stream: XMLStreamReader): TolerantType = this

    open fun getTypeName(): QName = getQualifiedName()

    open fun closeType(bindContext: BindContext, instance: Any) {

    }

}

