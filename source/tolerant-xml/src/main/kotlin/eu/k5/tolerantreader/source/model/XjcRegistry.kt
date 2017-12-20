package eu.k5.tolerantreader.source.model

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import java.lang.UnsupportedOperationException
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.xml.bind.JAXBElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlSchema
import javax.xml.bind.annotation.XmlType
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
        val annotation: XmlRootElement? = type.getAnnotation(XmlRootElement::class.java)
        var qname: QName? = null
        if (annotation != null) {
            qname = QName(annotation.namespace, annotation.name)
            val xjcType = XjcType(annotation != null, type, registry, qname)

            types.put(qname!!, xjcType)


        }


        val isRoot: Boolean
        val qName: QName
        if (type.getAnnotation(XmlRootElement::class.java) != null) {
            qName = QName(registry.namespace, type.getAnnotation(XmlRootElement::class.java).name)
            isRoot = true
        } else if (type.getAnnotation(XmlType::class.java) != null) {
            val typeAnnotation = type.getAnnotation(XmlType::class.java)

            qName = QName(registry.namespace, typeAnnotation.name)
            isRoot = false
        } else {
            qName = QName(registry.namespace, "xxx")
            isRoot = false
        }

        val xjcType = XjcType(isRoot, type, registry, qName)
        registry.types.add(xjcType)

        types.put(qName, xjcType)

    }


    fun getTypes(): Map<QName, XjcType> {
        return types
    }

    fun getElements(): List<XjcType> {
        return ArrayList(types.values)
    }

    fun getElementByClass(type: Class<*>): XjcType? {

        val byType = types.values.filter { it.type.equals(type) }

        return when {
            byType.isEmpty() -> null
            byType.size == 1 -> byType[0]
            else -> throw IllegalStateException("Not unique")
        }
    }


}

class XjcXmlRegistry(val packageName: String, val namespace: String) {
    val types = ArrayList<XjcType>()
}