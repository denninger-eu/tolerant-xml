package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.binding.TolerantWriter
import javax.xml.namespace.QName

class TolerantSchema(private val elements: Map<String, TolerantElement>, private val types: TolerantMap<TolerantComplexType>, private val writer: TolerantWriter) {


    fun createContext(): BindContext = writer.createContext(this)

    fun getElement(namespaceURI: String?, localName: String): TolerantElement? {
        return elements[localName]!!
    }

    fun getComplexType(name: QName): TolerantType {
        return types.getByLocalName(name.localPart)!!
    }


}