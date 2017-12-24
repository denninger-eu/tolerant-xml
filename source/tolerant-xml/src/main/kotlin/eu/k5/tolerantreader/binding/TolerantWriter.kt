package eu.k5.tolerantreader.binding

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.InitContext
import eu.k5.tolerantreader.tolerant.TolerantSchema
import javafx.scene.text.FontWeight
import javax.xml.namespace.QName

interface TolerantWriter {

    fun createSupplier(initContext: InitContext, typeName: QName): (elementName: QName) -> Any

    fun createEnumSupplier(initContext: InitContext, enumName: QName, literals: Collection<String>): EnumSupplier

    fun createElementAssigner(initContext: InitContext, entityType: QName, element: QName, target: QName, parameters: ElementParameters): Assigner

    fun rootAssigner(elementName: QName): Assigner

    fun createContext(schema: TolerantSchema): BindContext
}

class EnumSupplier(
        val targetType: QName,
        val parser: (BindContext, String) -> Any?
)

class ElementParameters(
        val list: Boolean,
        val weight: Int,
        val attribute: Boolean
)

