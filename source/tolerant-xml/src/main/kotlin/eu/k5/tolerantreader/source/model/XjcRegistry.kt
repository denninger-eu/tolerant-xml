package eu.k5.tolerantreader.source.model

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import java.lang.UnsupportedOperationException
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.xml.bind.JAXBElement
import javax.xml.bind.annotation.XmlSchema
import javax.xml.namespace.QName
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class XjcRegistry(seed: List<Class<*>>) {
    private val types: MutableMap<QName, XjcType> = HashMap()
    private val todo: Deque<Class<*>> = ArrayDeque()

    private val subtypes: Multimap<Class<*>, Class<*>> = HashMultimap.create()

    init {
        todo.addAll(seed)
    }

    fun init() {

        while (!todo.isEmpty()) {
            val type = todo.poll()
            val registry = handlePackage(type.`package`.name)


        }

    }

    private fun handlePackage(packageName: String): XjcXmlRegistry {


        val factory = Class.forName(packageName + ".ObjectFactory")


        val schema: XmlSchema = factory.`package`.getAnnotation(XmlSchema::class.java)

        val registry = XjcXmlRegistry(packageName, schema.namespace)

        for (method in factory.declaredMethods) {
            val returnType = method.genericReturnType
            if (returnType is ParameterizedType) {

                if (returnType.rawType.equals(JAXBElement::class.java)) {
                    handleType(registry, returnType.actualTypeArguments[0] as Class<*>)
                } else if (returnType.rawType.equals(List::class.java)) {

                } else {
                    throw UnsupportedOperationException("Unknown raw type: " + returnType.rawType)
                }

            } else if (returnType is Class<*>) {
                handleType(registry, returnType)
            } else {
                throw UnsupportedOperationException("unknown generictype: " + returnType.typeName);
            }
        }
        return registry
    }

    fun handleType(registry: XjcXmlRegistry, type: Class<*>) {

    }

    fun getElements(): List<XjcType> {
        return ArrayList()
    }


}

class XjcXmlRegistry(val packageName: String, val namespace: String) {
    val types = ArrayList<XjcType>()
}