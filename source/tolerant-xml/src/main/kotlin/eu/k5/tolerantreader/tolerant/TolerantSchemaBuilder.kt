package eu.k5.tolerantreader.tolerant

import com.google.common.collect.ImmutableMap
import eu.k5.tolerantreader.InitContext
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.xs.XsComplexType
import eu.k5.tolerantreader.xs.XsRegistry
import javax.xml.namespace.QName

class TolerantSchemaBuilder(private val xsRegistry: XsRegistry, private val writer: TolerantWriter) {
    private val xsdString = QName("http://www.w3.org/2001/XMLSchema", "string")


    private val simpleTypes: MutableMap<QName, TolerantSimpleType> = HashMap()


    private val complexTypes: MutableMap<QName, TolerantComplexType> = HashMap()

    private val complexTypeBuilders: MutableMap<QName, ComplexTypeBuilder> = HashMap()

    private val complexTypeBuilding: MutableSet<QName> = HashSet()

    private val elements: MutableMap<String, TolerantElement> = HashMap()

    private val initContext: InitContext = InitContext()

    private var tolerantComplexTypes: TolerantMap<TolerantComplexType>? = null

    private fun resolveType(name: QName): TolerantType {
        val simpleType = simpleTypes[name]
        if (simpleType != null) {
            return simpleType
        }
        if (complexTypes.containsKey(name)) {
            return complexTypes[name]!!
        }
        val complexType = complexTypeBuilders[name]
        if (complexType != null) {
            if (complexTypeBuilding.contains(name)) {
                return complexType.proxy
            }
            return finishComplexType(complexType)
        }
        TODO("add others")
    }

    fun build(): TolerantSchema {

        initBaseTypes()

        initSimpleTypes()

        prepareComplexTypes()

        finishComplexTypes()

        initElements()


        return TolerantSchema(elements!!, tolerantComplexTypes!!, writer)
    }

    private fun initBaseTypes() {
        simpleTypes.put(xsdString, TolerantStringType(xsdString))

    }


    private fun initSimpleTypes() {
        for (simpleType in xsRegistry.getAllSimpleTypes()) {
            val base = simpleType.restriction?.base

            if (xsdString.equals(base)) {

                val type = TolerantStringType(simpleType.getQualifiedName())

                simpleTypes.put(type.name, type)
            } else {
                TODO("not implement yet")
            }


        }
    }


    private fun prepareComplexTypes() {
        for (xsComplexType in xsRegistry.getAllComplexTypes()) {
            val qname = xsComplexType.getQualifiedName()
            val createSupplier = writer.createSupplier(qname)
            var builder = ComplexTypeBuilder(qname, createSupplier, xsComplexType, TolerantComplexProxy(qname))
            complexTypeBuilders[xsComplexType.getQualifiedName()] = builder
        }
    }

    private fun finishComplexTypes() {
        val mapBuilder: TolerantMapBuilder<TolerantComplexType> = TolerantMapBuilder()
        for (complexTypeBuilder in complexTypeBuilders) {

            val complexType = finishComplexType(complexTypeBuilder.value)

            mapBuilder.append(complexTypeBuilder.key, complexType)
        }
        tolerantComplexTypes = mapBuilder.build()
    }

    private fun finishComplexType(typeBuilder: ComplexTypeBuilder): TolerantComplexType {
        if (typeBuilder.isBuild()) {
            return typeBuilder.build()
        }

        val qualifiedName: QName = typeBuilder.name!!
        complexTypeBuilding.add(qualifiedName)


        for (attribute in typeBuilder.xsComplexType.getAllAttributes()) {
            val type = attribute.type!!
            val simpleType = resolveType(type)
            val assigner = writer.createAttributeAssigner(initContext, qualifiedName, attribute.name!!, simpleType.getTypeName())

            val qname = QName(qualifiedName.namespaceURI, attribute.name)

            typeBuilder.addElement(attribute.name!!, TolerantElement(qname, simpleType, assigner, true))
        }
        var elementWeight = 0
        for (element in typeBuilder.xsComplexType.getAllElememts()) {
            elementWeight++
            if (element.isLocalType()) {


            } else {
                val typeName = element.getTypeName()
                val tolerantType = resolveType(typeName)
                val assigner = writer.createElementAssigner(initContext, qualifiedName, element.getQualifiedName(), tolerantType.getTypeName(), element.isList(), elementWeight)

                val qname = QName(qualifiedName.namespaceURI, element.name)


                val tolerantElement = TolerantElement(qname, tolerantType, assigner, true)
                typeBuilder.addElement(element.name!!, tolerantElement)
            }
        }

        for (subType in typeBuilder.xsComplexType.getAllConcreteSubtypes()) {
            typeBuilder.addConcreteSubtype(subType)
        }
        complexTypeBuilding.remove(qualifiedName)
        return typeBuilder.build()
    }


    private fun createTolerantElement() {

    }

    private fun initElements() {
        for (element in xsRegistry.getAllElements()) {

            if (element.type != null) {
                val complexType = tolerantComplexTypes?.get(element.type!!)


                val bindElement = TolerantElement(element.getQualifiedName(), complexType!!, writer.rootAssigner(element.getQualifiedName()   ), false)

                elements.put(element.localName!!, bindElement)
            }

        }
    }
}


private class ComplexTypeBuilder(val name: QName, val konstructor: (QName) -> Any, val xsComplexType: XsComplexType, val proxy: TolerantComplexProxy) {

    val elements = ImmutableMap.builder<String, TolerantElement>()
    val concreteSubtypes = TolerantMapBuilder<QName>()
    var tolerantComplexType: TolerantComplexType? = null


    fun addElement(name: String, bindElement: TolerantElement) {
        elements.put(name, bindElement)
    }

    fun isBuild(): Boolean {
        return tolerantComplexType != null
    }

    fun addConcreteSubtype(complexTypeName: QName) {
        concreteSubtypes.append(complexTypeName, complexTypeName)
    }

    fun build(): TolerantComplexType {
        var complexType = tolerantComplexType;
        if (complexType != null) {
            return complexType
        }

        complexType = TolerantComplexType(name!!, konstructor, elements.build(), concreteSubtypes.build())
        proxy.delegate = complexType
        tolerantComplexType = complexType
        return complexType
    }
}
