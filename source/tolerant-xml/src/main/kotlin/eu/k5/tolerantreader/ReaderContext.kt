package eu.k5.tolerantreader

import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.reader.ViolationType
import eu.k5.tolerantreader.tolerant.TolerantType
import javax.xml.namespace.QName

interface ReaderContext {


    fun getComplexType(qName: QName): TolerantType

    fun getEntityById(id: String): Any?

    fun addViolation(type: ViolationType, value: String)

    fun retrieveComments(): List<String>

    fun addOpenIdRef(id: String, instance: Any, delegate: Assigner)

    fun registerEntity(id: String, instance: Any)

}