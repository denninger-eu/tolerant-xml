package eu.k5.tolerantreader.strict

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import eu.k5.tolerantreader.Reader
import eu.k5.tolerantreader.source.model.XjcRegistry
import eu.k5.tolerantreader.tolerant.TolerantMap
import eu.k5.tolerantreader.tolerant.TolerantMapBuilder
import eu.k5.tolerantreader.xs.XSD_NAMESPACE
import eu.k5.tolerantreader.xs.XsComplexType
import eu.k5.tolerantreader.xs.XsElement
import eu.k5.tolerantreader.xs.XsRegistry
import java.lang.reflect.Method
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.namespace.QName

class StrictSchemaBuilder(private val xjcRegistry: XjcRegistry, private val xsRegistry: XsRegistry) {

    private val simpleAdapters: MutableMap<QName, StrictTypeAdapter> = HashMap()
    private val simpleTypes: MutableMap<QName, StrictType> = HashMap()

    private val complexTypeBuilders = HashMap<QName, StrictComplexTypeBuilder>()

    private val strictComplexTypes = ImmutableMap.builder<Class<*>, StrictComplexTypeBuilder>()
    private val strictElements = ImmutableMap.builder<Class<*>, StrictElement>()

    private val elements: TolerantMap<XsElement>

    init {
        val mapBuilder = TolerantMapBuilder<XsElement>();
        for (element in xsRegistry.getAllElements()) {
            mapBuilder.append(element.getQualifiedName(), element);
        }
        this.elements = mapBuilder.build()
    }

    private fun initBaseTypes() {
        simpleAdapters.put(QName(XSD_NAMESPACE, "string"), ToStringAdapter)
    }

    private fun initSimpleTypes() {


        for (xsSimpleType in xsRegistry.getAllSimpleTypes()) {
            val qname = xsSimpleType.getQualifiedName()
            simpleAdapters.put(qname, ToStringAdapter)
        }
    }


    fun init() {
        initBaseTypes()
        initSimpleTypes()
        initComplexTypes()
        initElements()
    }

    private fun initElements() {
        for (element in xjcRegistry.getElements()) {
            val annotation = element.getAnnotation(XmlRootElement::class.java)
            val xsElement = elements.get(annotation.namespace, annotation.name)

            val xsComplexType = xsElement?.complexType

            val strictType = createComplexType(element, xsComplexType!!)


            val strictElement = StrictElement(xsElement!!.getQualifiedName(), strictType)
            strictElements.put(element, strictElement)
        }

    }

    private fun initComplexType(clazz: Class<*>, xsComplexType: XsComplexType) {

        strictComplexTypes
        val qname = xsComplexType.getQualifiedName()


        for (attribute in xsComplexType.getAllAttributes()) {

        }

        for (element in xsComplexType.getAllElememts()) {

        }

    }


    private fun resolveReader(baseClazz: Class<*>, name: String, expectedType: Class<*>) {

        val getterName = getterName(name)

        val method = baseClazz.getMethod(getterName)

    }


    private fun initComplexTypes() {



    }

    private fun createComplexType(clazz: Class<*>, xsComplexType: XsComplexType): StrictComplexType {

        val strictType = StrictComplexTypeBuilder(xsComplexType.getQualifiedName(), clazz)


        for (attribute in xsComplexType.getDeclaredAttributes()) {

            val adapter = simpleAdapters.get(attribute.type)!!

            val reader = createReader(clazz, attribute.name!!)
            val strictAttribute = StrictAttribute(attribute.name!!, reader, adapter)
            strictType.attributes.add(strictAttribute)
        }

        for (element in xsComplexType.getDeclaredElements()) {
            val reader = createReader(clazz, element.name!!)

            val get = simpleAdapters.get(element.type)
            if (get != null) {
                val strictElement = StrictSimpleElementType(element.getQualifiedName(), get)
                val strictComplexElement = StrictComplexElement(element.name!!, reader, strictElement)
                strictType.elements.add(strictComplexElement)
            } else {

            }


        }



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
        return StrictSchema(strictElements.build())

    }

}

class GetterReader(val getter: Method) : Reader {

    override fun read(instance: Any): Any {

        return getter.invoke(instance)
    }

}

class StrictComplexTypeBuilder(val name: QName, val clazz: Class<*>) {
    val attributes = ImmutableList.builder<StrictAttribute>()
    val elements = ImmutableList.builder<StrictComplexElement>()

    fun build(): StrictComplexType {
        return StrictComplexType(attributes.build(), elements.build())
    }
}