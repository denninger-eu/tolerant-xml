package eu.k5.tolerantreader.binding.model

import eu.k5.tolerantreader.*
import eu.k5.tolerantreader.tolerant.TolerantSchema
import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.binding.TolerantWriter
import java.lang.reflect.Method
import javax.xml.datatype.XMLGregorianCalendar
import javax.xml.namespace.QName

class Binder(private val packageMapping: PackageMapping) : TolerantWriter {


    private val utils = ReflectionUtils()
    private val classCache: MutableMap<QName, Class<*>> = HashMap()

    init {
        classCache.put(xsString, java.lang.String::class.java)
        classCache.put(xsBase64Binary, ByteArray::class.java)
        classCache.put(xsBoolean, Boolean::class.java)
        classCache.put(xsHexBinary, ByteArray::class.java)
        classCache.put(xsQname, QName::class.java)
        classCache.put(xsDate, XMLGregorianCalendar::class.java)
        classCache.put(xsDatetime, XMLGregorianCalendar::class.java)
        classCache.put(xsDuration, XMLGregorianCalendar::class.java)
        classCache.put(xsGDay, XMLGregorianCalendar::class.java)
        classCache.put(xsGMonth, XMLGregorianCalendar::class.java)
        classCache.put(xsGMonthDay, XMLGregorianCalendar::class.java)
        classCache.put(xsGYear, XMLGregorianCalendar::class.java)
        classCache.put(xsGYearMonth, XMLGregorianCalendar::class.java)
        classCache.put(xsTime, XMLGregorianCalendar::class.java)


    }

    private fun resolveJavaClass(name: QName): Class<*> {
        if (classCache.containsKey(name)) {
            return classCache[name]!!
        }
        val packge = packageMapping.getPackage(name.namespaceURI)

        val klassName = utils.sanitizeAsClassName(name.localPart)
        val klass = Class.forName(packge + "." + klassName)
        classCache.put(name, klass)
        return klass
    }


    override fun createSupplier(base: QName): (QName) -> Any {
        val constructor = resolveJavaClass(base).getConstructor()
        return { constructor.newInstance() }
    }

    override fun createAttributeAssigner(initContext: InitContext, qualifiedName: QName, name: String, typeName: QName): Assigner {
        return createElementAssigner(initContext, qualifiedName, QName(qualifiedName.namespaceURI, name), typeName, false, 0)
    }

    override fun createElementAssigner(initContext: InitContext, base: QName, element: QName, target: QName, list: Boolean, weight: Int): Assigner {

        val baseClass = resolveJavaClass(base)
        val propertyClass = resolveJavaClass(target)

        if (list) {
            return createListAppendAssigner(initContext, baseClass, propertyClass, element.localPart)
        } else {
            return createSetterAssigner(initContext, baseClass, propertyClass, element.localPart)
        }

    }

    override fun createContext(schema: TolerantSchema): BindContext = BindContext(schema, BindRoot())

    override fun rootAssigner(elementName: QName): Assigner = BindRootAssigner


    private fun createListAppendAssigner(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: String): Assigner {
        try {
            val getter = baseClass.getMethod(utils.getGetterName(element))
            // TODO: validate getter return type
            return ListAppendAssigner(getter)
        } catch (exception: Exception) {
            return NoopAssigner
        }
    }

    private fun createSetterAssigner(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: String): Assigner {
        try {
            val setter = baseClass.getMethod(utils.getSetterName(element), propertyClass)
            return SetterAssigner(setter)
        } catch (exception: Exception) {
            return NoopAssigner
        }
    }


}

object NoopAssigner : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {

    }
}

object BindRootAssigner : Assigner {

    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is BindRoot) {
            instance.instance = value
            return
        }
        TODO("add error logging for wrong root instance type")
    }

}

class SetterAssigner(private val setter: Method) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        setter.invoke(instance, value)
    }
}

class ListAppendAssigner(private val getter: Method) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {

        val obj = getter.invoke(instance)
        @Suppress("UNCHECKED_CAST")
        val list = obj as MutableList<Any?>
        list.add(value)
    }
}