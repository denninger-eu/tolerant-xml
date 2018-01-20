package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.ReaderContext
import eu.k5.tolerantreader.reader.BindContext
import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamReader


abstract class TolerantType() {

    abstract fun getQualifiedName(): QName

    abstract fun readValue(context: ReaderContext, elementName: TolerantElement, stream: XMLStreamReader): Any?

    abstract fun pushedOnStack(): Boolean

    open fun asSubtype(context: ReaderContext, stream: XMLStreamReader): TolerantType = this

    open fun getTypeName(): QName = getQualifiedName()

    open fun closeType(bindContext: ReaderContext, instance: Any) {

    }

}

