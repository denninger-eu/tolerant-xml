package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.RootElement
import eu.k5.tolerantreader.reader.BindContext
import eu.k5.tolerantreader.reader.TolerantReaderConfiguration
import eu.k5.tolerantreader.binding.TolerantWriter
import javax.xml.namespace.QName

class TolerantSchema(

        private val elements: TolerantMap<TolerantElement>,
        private val types: TolerantMap<TolerantComplexType>,
        private val rootSupplier: () -> RootElement,
        private val transformer: TolerantMap<Map<String, TolerantTransformer>>

) {


    fun createContext(readerConfig: TolerantReaderConfiguration): BindContext {
        val root = rootSupplier()
        return BindContext(this, root, readerConfig)
    }

    fun getElement(namespaceURI: String, localName: String): TolerantElement?
            = elements.get(namespaceURI, localName)


    fun getComplexType(name: QName): TolerantType
            = types.getByLocalName(name.localPart)!!

    fun getTransformer(typeName: QName, transformerName: String): TolerantTransformer?
            = transformer.get(typeName)?.get(transformerName)

}