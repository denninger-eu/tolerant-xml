package eu.k5.tolerantreader.strict

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import eu.k5.tolerantreader.Reader
import eu.k5.tolerantreader.source.model.XjcRegistry
import eu.k5.tolerantreader.tolerant.TolerantMap
import eu.k5.tolerantreader.tolerant.TolerantMapBuilder
import eu.k5.tolerantreader.XSD_NAMESPACE
import eu.k5.tolerantreader.source.model.XjcType
import eu.k5.tolerantreader.tolerant.XSI_NAMESPACE
import eu.k5.tolerantreader.xs.XsComplexType
import eu.k5.tolerantreader.xs.XsElement
import eu.k5.tolerantreader.xs.XsRegistry
import java.lang.reflect.Method
import javax.xml.namespace.QName

class StrictSchemaBuilder(private val xjcRegistry: XjcRegistry, private val xsRegistry: XsRegistry) {

    private val simpleAdapters: MutableMap<QName, StrictTypeAdapter> = HashMap()

    private val namespaces: MutableSet<String> = HashSet()

    private val strictComplexTypes = HashMap<Class<*>, StrictComplexTypeBuilder>()
    private val strictElements = ImmutableMap.builder<Class<*>, StrictElement>()

    private val xsElements: TolerantMap<XsElement>

    private val xsComplexTypes: TolerantMap<XsComplexType>

    init {

        val mapBuilder = TolerantMapBuilder<XsElement>(false)
        for (element in xsRegistry.getAllElements()) {
            mapBuilder.append(element.getQualifiedName(), element);
        }
        this.xsElements = mapBuilder.build()


        val ctMapBuilder = TolerantMapBuilder<XsComplexType>(false)
        for (element in xsRegistry.getAllComplexTypes()) {
            ctMapBuilder.append(element.getQualifiedName(), element);
        }
        this.xsComplexTypes = ctMapBuilder.build()

    }

    private fun initBaseTypes() {
        simpleAdapters.put(QName(XSD_NAMESPACE, "string"), ToStringAdapter)
    }

    private fun initSimpleTypes() {


        for (xsSimpleType in xsRegistry.getAllSimpleTypes()) {
            val qname = xsSimpleType.getQualifiedName()
            simpleAdapters.put(qname, ToStringAdapter)
            namespaces.add(qname.namespaceURI)
        }
    }


    fun init() {
        initBaseTypes()
        initSimpleTypes()
        initComplexTypes()
        initElements()
    }

    private fun initElements() {
        for (element in xjcRegistry.getTypes().values) {


            val xsElement = xsElements.get(element.qName)

            val xsComplexType = xsElement?.complexType

            if (xsComplexType == null) {
                continue
            }

            val strictComplexType = createComplexType(element, xsComplexType!!)

            strictElements.put(strictComplexType.type, StrictElement(xsElement.getQualifiedName(), strictComplexType))

            namespaces.add(xsElement.getQualifiedName().namespaceURI)
        }

    }


    private fun resolveReader(baseClazz: Class<*>, name: String, expectedType: Class<*>) {

        val getterName = getterName(name)

        val method = baseClazz.getMethod(getterName)

    }


    private fun initComplexTypes() {
        for (type in xjcRegistry.getTypes().values) {


            val xsComplexType = xsComplexTypes.get(type.qName)

            if (xsComplexType == null) {
                continue
            }


            createComplexType(type, xsComplexType)
        }

    }

    private fun createComplexType(type: XjcType, xsComplexType: XsComplexType): StrictComplexType {
        val clazz = type.type
        val strictType = StrictComplexTypeBuilder(xsComplexType.getQualifiedName(), type)

        strictComplexTypes.put(type.type, strictType)

        for (field in type.type.declaredFields) {

            val attribute = xsComplexType.getAttributeByName(field.name)

            if (attribute != null) {
                val adapter = simpleAdapters.get(attribute.type)!!

                val reader = createReader(clazz, attribute.name!!)
                val strictAttribute = StrictAttribute(attribute.name!!, reader, adapter)
                strictType.attributes.add(strictAttribute)
                continue
            }

            val element = xsComplexType.getElementByName(field.name)

            if (element != null) {
                val reader = createReader(clazz, element.name!!)

                val get = simpleAdapters.get(element.type)
                if (get != null) {
                    val strictElement = StrictSimpleElementType(element.getQualifiedName(), get)
                    val strictComplexElement = StrictComplexElement(element.name!!, reader, strictElement, field.type)
                    strictType.elements.add(strictComplexElement)
                } else {
                    val get1 = strictComplexTypes.get(field.type)
                    if (get1 != null) {
                        val strictComplexElement = StrictComplexElement(element.name!!, reader, get1.proxy, field.type)
                        strictType.elements.add(strictComplexElement)
                    }


                }
            }
        }
        namespaces.add(strictType.name.namespaceURI)
        return strictType.build()
    }


    private fun createReader(clazz: Class<*>, attribute: String): Reader {
        val getter = clazz.getMethod(getterName(attribute))
        return GetterReader(getter)
    }

    private fun getterName(property: String): String {
        return "get" + property[0].toUpperCase() + property.substring(1, property.length)
    }

    fun build(): StrictSchema {
        init()

        val typesBuilder = ImmutableMap.builder<Class<*>, StrictComplexType>()
        for ((key, type) in strictComplexTypes) {
            typesBuilder.put(key, type.build())
        }
        val namespacePrefixes = ImmutableMap.builder<String, String>()
        var index = 0
        for (namespace in namespaces) {
            namespacePrefixes.put(namespace, "xs" + index++)
        }
        namespacePrefixes.put(XSI_NAMESPACE, "xsi")



        return StrictSchema(strictElements.build(), typesBuilder.build(), namespacePrefixes.build())
    }

}

class GetterReader(val getter: Method) : Reader {

    override fun read(instance: Any): Any {

        return getter.invoke(instance)
    }

}

class StrictComplexTypeBuilder(val name: QName, val type: XjcType) {
    val proxy = StrictComplexProxy(name)
    val attributes = ImmutableList.builder<StrictAttribute>()
    val elements = ImmutableList.builder<StrictComplexElement>()

    var build: StrictComplexType? = null

    fun build(): StrictComplexType {
        if (build != null) {

        }
        val build = StrictComplexType(type.type, name, attributes.build(), elements.build())
        proxy.delegate = build
        return build
    }
}