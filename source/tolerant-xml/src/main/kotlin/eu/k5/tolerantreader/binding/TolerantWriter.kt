package eu.k5.tolerantreader.binding

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.InitContext
import eu.k5.tolerantreader.tolerant.TolerantSchema
import javafx.scene.text.FontWeight
import javax.xml.namespace.QName

interface TolerantWriter {

    fun createSupplier(typeName: QName): (elementName: QName) -> Any

    fun createElementAssigner(initContext: InitContext, base: QName, element: QName, target: QName, list: Boolean, weight: Int): Assigner

    fun createAttributeAssigner(initContext: InitContext, qualifiedName: QName, name: String, typeName: QName): Assigner

    fun noopAssigner(): Assigner = NoopAssigner

    fun rootAssigner(elementName: QName): Assigner
    fun createContext(schema: TolerantSchema): BindContext

}

