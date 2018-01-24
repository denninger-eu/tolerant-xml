package eu.k5.tolerantreader.binding

import eu.k5.tolerantreader.reader.BindContext
import eu.k5.tolerantreader.InitContext
import eu.k5.tolerantreader.ReaderContext
import eu.k5.tolerantreader.RootElement
import eu.k5.tolerantreader.reader.TolerantReaderConfiguration
import eu.k5.tolerantreader.tolerant.TolerantSchema
import javax.xml.namespace.QName

interface TolerantWriter {

    fun createCloser(initContext: InitContext): Closer

    fun createSupplier(initContext: InitContext, typeName: QName): (elementName: QName) -> Any

    fun createEnumSupplier(initContext: InitContext, enumName: QName, literals: Collection<String>): EnumSupplier

    fun createElementAssigner(initContext: InitContext, entityType: QName, element: QName, target: QName, parameters: ElementParameters): Assigner

    fun rootAssigner(elementName: QName): Assigner

    fun createRootElementSupplier(): () -> RootElement

    fun createElementRetriever(initContext: InitContext, qName: QName)

}

class EnumSupplier(
        val targetType: QName,
        val parser: (ReaderContext, String) -> Any?
)

class ElementParameters(
        val list: Boolean,
        val weight: Int,
        val attribute: Boolean
)

