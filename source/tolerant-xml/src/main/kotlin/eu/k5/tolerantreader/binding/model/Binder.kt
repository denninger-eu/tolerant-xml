package eu.k5.tolerantreader.binding.model

import eu.k5.tolerantreader.*
import eu.k5.tolerantreader.binding.*
import eu.k5.tolerantreader.reader.ViolationType
import eu.k5.tolerantreader.tolerant.IdRefType
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.math.BigDecimal
import java.math.BigInteger
import javax.xml.bind.JAXBElement
import javax.xml.datatype.Duration
import javax.xml.datatype.XMLGregorianCalendar
import javax.xml.namespace.QName

class NoOpCloser : Closer {
    override fun close(bindContext: ReaderContext, instance: Any) {
    }

}

class Binder(private val packageMapping: PackageMapping) : TolerantWriter {


    override fun createCloser(initContext: InitContext): Closer = NoOpCloser()


    private val utils = ReflectionUtils()
    private val classCache: MutableMap<QName, Class<*>> = HashMap()

    init {
        classCache[xsString] = java.lang.String::class.java
        classCache[xsBase64Binary] = ByteArray::class.java
        classCache[xsBoolean] = Boolean::class.java
        classCache[xsHexBinary] = ByteArray::class.java
        classCache[xsQname] = QName::class.java
        classCache[xsDate] = XMLGregorianCalendar::class.java
        classCache[xsDatetime] = XMLGregorianCalendar::class.java
        classCache[xsDuration] = Duration::class.java
        classCache[xsGDay] = XMLGregorianCalendar::class.java
        classCache[xsGMonth] = XMLGregorianCalendar::class.java
        classCache[xsGMonthDay] = XMLGregorianCalendar::class.java
        classCache[xsGYear] = XMLGregorianCalendar::class.java
        classCache[xsGYearMonth] = XMLGregorianCalendar::class.java
        classCache[xsTime] = XMLGregorianCalendar::class.java


        classCache[xsByte] = Byte::class.java
        classCache[xsDecimal] = BigDecimal::class.java
        classCache[xsInt] = Int::class.java
        classCache[xsInteger] = BigInteger::class.java
        classCache[xsLong] = Long::class.java
        classCache[xsNegativeInteger] = BigInteger::class.java
        classCache[xsNonNegativeInteger] = BigInteger::class.java
        classCache[xsNonPositiveInteger] = BigInteger::class.java
        classCache[xsPositiveInteger] = BigInteger::class.java
        classCache[xsShort] = Short::class.java
        classCache[xsUnsignedLong] = BigInteger::class.java
        classCache[xsUnsignedInt] = Long::class.java
        classCache[xsUnsignedShort] = Int::class.java
        classCache[xsUnsignedByte] = Short::class.java
        classCache[xsDouble] = Double::class.java
        classCache[xsFloat] = Float::class.java


        classCache[xsIdRef] = Object::class.java
        classCache[xsId] = String::class.java
    }

    private fun resolveJavaClass(name: QName): Class<*> {
        if (classCache.containsKey(name)) {
            return classCache[name]!!
        }
        val packge = packageMapping.getPackage(name.namespaceURI)

        val klassName = utils.sanitizeAsClassName(name.localPart)
        val klass = Class.forName(packge + "." + klassName)
        classCache[name] = klass
        return klass
    }


    override fun createSupplier(initContext: InitContext, typeName: QName): (QName) -> Any {
        val constructor = resolveJavaClass(typeName).getConstructor()
        return { constructor.newInstance() }
    }

    override fun createEnumSupplier(initContext: InitContext, enumName: QName, literals: Collection<String>): EnumSupplier {
        val enumClass = resolveJavaClass(enumName)
        val factoryMethod = enumClass.getMethod("fromValue", String::class.java)


        return EnumSupplier(enumName) { context: ReaderContext, token: String ->
            try {
                factoryMethod.invoke(null, token)
            } catch (exception: InvocationTargetException) {
                context.addViolation(ViolationType.INVALID_ENUM_LITERAL, token)
                null
            }
        }
    }


    override fun createElementAssigner(initContext: InitContext, entityType: QName, element: QName, target: QName, parameters: ElementParameters): Assigner {

        val baseClass = resolveJavaClass(entityType)
        val propertyClass = resolveJavaClass(target)


        val assigner =
                if (parameters.list) {
                    createListAppendAssigner(initContext, baseClass, propertyClass, element.localPart)
                } else {
                    createSetterAssigner(initContext, baseClass, propertyClass, element)
                }


        if (target == xsIdRef) {
            return IdRefAssigner(assigner)
        } else if (target == xsId) {
            return IdAssigner(assigner)
        }
        return assigner
    }


    override fun createElementRetriever(initContext: InitContext, entityType: QName, element: QName, targetName: QName, parameters: ElementParameters): Retriever {
        if (parameters.list) {
            return NoOpRetriever
        }
        val baseClass = resolveJavaClass(entityType)
//        val propertyClass = resolveJavaClass(targetName)
        return createGetterRetriever(initContext, baseClass, element.localPart)
    }

    private fun createGetterRetriever(initContext: InitContext, baseClass: Class<*>, element: String): Retriever {
        try {
            val getter = baseClass.getMethod(utils.getGetterName(element))

            return GetterRetriever(getter)

        } catch (exception: Exception) {
            initContext.addFinding(Type.MISSING_GETTER, exception.message ?: "")
            return NoOpRetriever
        }
    }


    override fun createRootElementSupplier(): () -> RootElement = { BindRoot() }

    override fun rootAssigner(elementName: QName): Assigner = BindRootAssigner

    private fun createListAppendAssigner(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: String): Assigner {
        try {
            val getter = baseClass.getMethod(utils.getGetterName(element))
            // TODO: validate getter return type
            return ListAppendAssigner(getter)
        } catch (exception: Exception) {
            initContext.addFinding(Type.MISSING_GETTER, exception.message ?: "")
            return NoOpAssigner
        }
    }

    private fun createSetterAssigner(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: QName): Assigner {
        try {
            if (hasBasicSetter(initContext, baseClass, propertyClass, element.localPart)) {
                return createBasicSetterAssigner(initContext, baseClass, propertyClass, element.localPart)
            }
            if (hasJaxbElementSetter(initContext, baseClass, propertyClass, element)) {
                return createJaxbElementSetterAssigner(initContext, baseClass, propertyClass, element)
            }
            initContext.addFinding(Type.MISSING_SETTER, element.toString())

            return NoOpAssigner
        } catch (exception: Exception) {
            initContext.addFinding(Type.MISSING_SETTER, element.localPart)
            return NoOpAssigner
        }
    }

    private fun createBasicSetterAssigner(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: String): Assigner {
        val setter = baseClass.getMethod(utils.getSetterName(element), propertyClass)
        return BasicSetterAssigner(setter)
    }

    private fun createJaxbElementSetterAssigner(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: QName): Assigner {
        val setter = baseClass.getMethod(utils.getSetterName(element.localPart), JAXBElement::class.java)

        return JaxbElementSetterAssigner(setter) { JAXBElement(element, propertyClass as Class<Any?>, propertyClass.cast(it)) }
    }

    private fun hasBasicSetter(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: String): Boolean {
        return try {
            baseClass.getMethod(utils.getSetterName(element), propertyClass)
            true
        } catch (exception: NoSuchMethodException) {
            false
        }
    }

    private fun hasJaxbElementSetter(initContext: InitContext, baseClass: Class<*>, propertyClass: Class<*>, element: QName): Boolean {
        try {
            val method = baseClass.getMethod(utils.getSetterName(element.localPart), JAXBElement::class.java)
            val parameter = method.genericParameterTypes[0]
                    as? ParameterizedType ?: return false

            return (parameter.actualTypeArguments[0] as Class<*>).isAssignableFrom(propertyClass)
        } catch (exception: NoSuchMethodException) {
            return false
        }
    }


}

object NoOpAssigner : Assigner {
    private val LOGGER = LoggerFactory.getLogger(NoOpAssigner::class.java)
    override fun assign(context: ReaderContext, instance: Any, value: Any?) {
        LOGGER.debug("Usage for NOOP Assigner")
    }
}

object NoOpRetriever : Retriever {
    private val LOGGER = LoggerFactory.getLogger(NoOpRetriever::class.java)
    override fun retrieve(context: ReaderContext, instance: Any): Any? {
        LOGGER.debug("Usage of NoOp Retriever")
        return null
    }

}

object BindRootAssigner : Assigner {

    override fun assign(context: ReaderContext, instance: Any, value: Any?) {
        if (instance is BindRoot) {
            instance.instance = value
            return
        }
        TODO("add error logging for wrong root instance type")
    }

}

class BasicSetterAssigner(private val setter: Method) : Assigner {
    override fun assign(context: ReaderContext, instance: Any, value: Any?) {
        try {
            setter.invoke(instance, value)
        } catch (exception: IllegalArgumentException) {
            context.addViolation(ViolationType.TYPE_MISMATCH, exception.message!!)
        }
    }
}

class JaxbElementSetterAssigner(private val setter: Method, private val jaxb: (Any?) -> JAXBElement<*>) : Assigner {
    override fun assign(context: ReaderContext, instance: Any, value: Any?) {

        setter.invoke(instance, jaxb(value))

    }
}

class ListAppendAssigner(private val getter: Method) : Assigner {
    override fun assign(context: ReaderContext, instance: Any, value: Any?) {

        val obj = getter.invoke(instance)

        @Suppress("UNCHECKED_CAST")
        val list = obj as MutableList<Any?>
        list.add(value)
    }
}

class IdRefAssigner(private val delegate: Assigner) : Assigner {

    override fun assign(context: ReaderContext, instance: Any, value: Any?) {
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
    override fun assign(context: ReaderContext, instance: Any, value: Any?) {
        if (value !is String) {
            TODO("Find proper error handling here")
        }
        delegate.assign(context, instance, value)
        context.registerEntity(value, instance)

    }

}

class GetterRetriever(private val getter: Method) : Retriever {
    override fun retrieve(context: ReaderContext, instance: Any): Any? {
        return try {
            getter.invoke(instance)
        } catch (exception: Exception) {
            context.addViolation(ViolationType.INVALID_CLASS_STRUCTURE, exception.message ?: "No message")
            null
        }
    }

}