package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.TolerantReaderConfiguration
import eu.k5.tolerantreader.binding.TolerantWriter
import javax.xml.namespace.QName

class TolerantSchema(private val elements: TolerantMap<TolerantElement>, private val types: TolerantMap<TolerantComplexType>, private val writer: TolerantWriter) {


    fun createContext(readerConfig: TolerantReaderConfiguration): BindContext
            = writer.createContext(this, readerConfig)

    fun getElement(namespaceURI: String, localName: String): TolerantElement? {
        return elements.get(namespaceURI, localName)
    }

    fun getComplexType(name: QName): TolerantType {
        return types.getByLocalName(name.localPart)!!
    }


}