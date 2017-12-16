package eu.k5.tolerantreader.binding.model

import eu.k5.tolerantreader.*
import eu.k5.tolerantreader.tolerant.TolerantSchema
import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.binding.ElementParameters
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.tolerant.IdRefType
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.math.BigDecimal
import java.math.BigInteger
import javax.xml.datatype.Duration
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
        classCache.put(xsDuration, Duration::class.java)
        classCache.put(xsGDay, XMLGregorianCalendar::class.java)
        classCache.put(xsGMonth, XMLGregorianCalendar::class.java)
        classCache.put(xsGMonthDay, XMLGregorianCalendar::class.java)
        classCache.put(xsGYear, XMLGregorianCalendar::class.java)
        classCache.put(xsGYearMonth, XMLGregorianCalendar::class.java)
        classCache.put(xsTime, XMLGregorianCalendar::class.java)


        classCache.put(xsByte, Byte::class.java)
        classCache.put(xsDecimal, BigDecimal::class.java)
        classCache.put(xsInt, Int::class.java)
        classCache.put(xsInteger, BigInteger::class.java)
        classCache.put(xsLong, Long::class.java)
        classCache.put(xsNegativeInteger, BigInteger::class.java)
        classCache.put(xsNonNegativeInteger, BigInteger::class.java)
        classCache.put(xsNonPositiveInteger, BigInteger::class.java)
        classCache.put(xsPositiveInteger, BigInteger::class.java)
        classCache.put(xsShort, Short::class.java)
        classCache.put(xsUnsignedLong, BigInteger::class.java)
        classCache.put(xsUnsignedInt, Long::class.java)
        classCache.put(xsUnsignedShort, Int::class.java)
        classCache.put(xsUnsignedByte, Short::class.java)
        classCache.put(xsDouble, Double::class.java)
        classCache.put(xsFloat, Float::class.java)


        classCache.put(xsIdRef, Object::class.java)
        classCache.put(xsId, String::class.java)
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



    override fun createElementAssigner(initContext: InitContext, entityName: QName, element: QName, target: QName, parameters: ElementParameters): Assigner {

        val baseClass = resolveJavaClass(entityName)
        val propertyClass = resolveJavaClass(target)


        val assigner =
                if (parameters.list) {
                    createListAppendAssigner(initContext, baseClass, propertyClass, element.localPart)
                } else {
                    createSetterAssigner(initContext, baseClass, propertyClass, element.localPart)
                }


        if (target == xsIdRef) {
            return IdRefAssigner(assigner)
        } else if (target == xsId) {
            return IdAssigner(assigner)
        }
        return assigner
    }

    override fun createContext(schema: TolerantSchema): BindContext = BindContext(schema, BindRoot())

    override fun rootAssigner(elementName: QName): Assigner = BindRootAssigner

    private fun createListAppendAssigner(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: String): Assigner {
        try {
            val getter = baseClass.getMethod(utils.getGetterName(element))
            // TODO: validate getter return type
            return ListAppendAssigner(getter)
        } catch (exception: Exception) {
            return NoOpAssigner
        }
    }

    private fun createSetterAssigner(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: String): Assigner {
        try {
            val setter = baseClass.getMethod(utils.getSetterName(element), propertyClass)
            return SetterAssigner(setter)
        } catch (exception: Exception) {
            initContext.addFinding(Type.MISSING_SETTER, element)
            return NoOpAssigner
        }
    }


}

object NoOpAssigner : Assigner {
    private val LOGGER = LoggerFactory.getLogger(NoOpAssigner::class.java)
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        LOGGER.debug("Usage for NOOP Assigner")
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
        try {
            setter.invoke(instance, value)
        } catch (exception: IllegalArgumentException) {
            context.addViolation(Violation.TYPE_MISMATCH, exception.message!!)
        }
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

class IdRefAssigner(private val delegate: Assigner) : Assigner {

    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (value !is IdRefType) {
            TODO("Find proper error handling here")
        }
        if (value.entity != null) {
            delegate.assign(context, instance, value.entity)
        } else {
            context.addOpenIdRef(value.id, instance, delegate)
        }

    }
}

class IdAssigner(private val delegate: Assigner) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (value !is String) {
            TODO("Find proper error handling here")
        }
        delegate.assign(context, instance, value)
        context.registerEntity(value, instance)

    }

}