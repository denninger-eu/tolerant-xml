package eu.k5.tolerantreader.tolerant

import com.google.common.collect.ImmutableMap
import eu.k5.tolerantreader.*
import eu.k5.tolerantreader.binding.Closer
import eu.k5.tolerantreader.binding.ElementParameters
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.binding.model.NoOpRetriever
import eu.k5.tolerantreader.transformer.Transformers
import eu.k5.tolerantreader.xs.XsComplexType
import eu.k5.tolerantreader.xs.XsRegistry
import javax.xml.namespace.QName

class TolerantSchemaBuilder(
        private val initContext: InitContext,
        private val xsRegistry: XsRegistry,
        private val writer: TolerantWriter,
        private val transformers: Transformers = Transformers()
) {


    private val simpleTypes: MutableMap<QName, TolerantSimpleType> = HashMap()

    private val complexTypes: MutableMap<QName, TolerantComplexType> = HashMap()

    private val complexTypeBuilders: MutableMap<QName, ComplexTypeBuilder> = HashMap()

    private val complexTypeBuilding: MutableSet<QName> = HashSet()

    private val elements: MutableMap<QName, TolerantElement> = HashMap()

    private val baseTypes = TolerantBaseTypes(initContext)

    private var tolerantComplexTypes: TolerantMap<TolerantComplexType>? = null

    private fun resolveType(name: QName): TolerantType {
        val simpleType = getSimpleType(name)
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
        TODO("add others: " + name)
    }

    fun build(): TolerantSchema {

        initSimpleTypes()

        prepareComplexTypes()

        finishComplexTypes()

        initElements()

        val transformer = getTransformer()
        return TolerantSchema(TolerantMap.of(elements.values) { it.qname }, tolerantComplexTypes!!, writer.createRootElementSupplier(), transformer)
    }

    private fun getTransformer(): TolerantMap<Map<String, TolerantTransformer>> {
        val byType = HashMap<QName, Map<String, TolerantTransformer>>()
        for (transformer in transformers.transformers) {

            val complexType = tolerantComplexTypes!!.get(transformer.type!!)

            if (complexType == null) {
                initContext.addFinding(Type.MISSING_TYPE_TRANSFORMER, transformer.type.toString())
            } else {
                val tolerantTransformer = TolerantTransformer(transformer.element!!, transformer.getTargetPath())
                val byElement = byType.computeIfAbsent(complexType.getQualifiedName()) { HashMap() } as MutableMap
                byElement.put(transformer.element!!, tolerantTransformer)

                for (concreteSubType in complexType.concreteSubtypes.values()) {
                    val byElement = byType.computeIfAbsent(concreteSubType) { HashMap() } as MutableMap
                    byElement.put(transformer.element!!, tolerantTransformer)
                }
            }

        }
        return TolerantMap.of(byType)
    }

    private fun getSimpleType(qName: QName): TolerantSimpleType? {
        val baseType = baseTypes.getBaseType(qName)
        if (baseType != null) {
            return baseType
        }
        return simpleTypes[qName]
    }

    private fun initSimpleTypes() {
        for (simpleType in xsRegistry.getAllSimpleTypes()) {

            val base = simpleType.restriction?.base

            if (simpleType.isEnum()) {

                val literals = simpleType.enumLiterals()

                val enumSupplier = writer.createEnumSupplier(initContext, simpleType.qualifiedName, literals)
                simpleTypes[simpleType.qualifiedName] = TolerantEnumType(simpleType.qualifiedName, enumSupplier)


            } else {

                val baseType = getSimpleType(simpleType.restriction!!.base!!)

                if (baseType != null) {
                    val type = TolerantSimpleRestriction(simpleType.qualifiedName, baseType)
                    simpleTypes[type.getQualifiedName()] = type
                }

/*                if (xsString.equals(base)) {

                    val type = TolerantStringType(simpleType.getQualifiedName())

                }*/
                else {
                    initContext.addFinding(Type.UNSUPPORTED_BASE_TYPE, base.toString())
                }
            }


        }
    }


    private fun prepareComplexTypes() {
        for (xsComplexType in xsRegistry.getAllComplexTypes()) {
            prepareComplexType(xsComplexType)
        }
        for (xsElement in xsRegistry.getAllElements()) {
            if (xsElement.complexType != null) {
                prepareComplexType(xsElement.complexType!!)
            }
        }
    }

    private fun prepareComplexType(xsComplexType: XsComplexType) {
        val qName = xsComplexType.qualifiedName
        val createSupplier = writer.createSupplier(initContext, qName)
        var builder = ComplexTypeBuilder(qName, createSupplier, xsComplexType, TolerantComplexProxy(qName), writer.createCloser(initContext))
        complexTypeBuilders[xsComplexType.qualifiedName] = builder

        val simpleContent = xsComplexType.simpleContent
        if (simpleContent != null) {
            val baseName = simpleContent.extension?.base

            val baseType = getSimpleType(baseName!!)

            val parameters = ElementParameters(false, 0, false)

            val elementName = QName(XSD_NAMESPACE, "value")

            val assigner = writer.createElementAssigner(initContext, qName, elementName, baseType!!.getTypeName(), parameters)

            builder.simpleContext = TolerantSimpleContent(baseType!!, assigner)


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
            if (attribute.type != null) {
                val type = attribute.type!!
                val simpleType = resolveType(type)
                val parameters = ElementParameters(false, 0, true)
                val qName = attribute.getQualifiedName()
                val assigner = writer.createElementAssigner(initContext, qualifiedName, qName, simpleType.getTypeName(), parameters)

                val retriever = writer.createElementRetriever(initContext, qualifiedName, qName, simpleType.getTypeName(), parameters)

                typeBuilder.addElement(attribute.name!!, TolerantElement(qName, simpleType, assigner, retriever, true))
            } else {
                initContext.addFinding(Type.UNKNOWN_SCHEMA, "Attribute without type")
            }
        }
        var elementWeight = 0
        for (element in typeBuilder.xsComplexType.getAllElememts()) {
            elementWeight++
            if (element.isLocalType()) {


            } else {
                val typeName = element.getTypeName()
                val tolerantType = resolveType(typeName)

                val parameters = ElementParameters(element.isList(), elementWeight, false)

                val assigner = writer.createElementAssigner(initContext, qualifiedName, element.qualifiedName, tolerantType.getTypeName(), parameters)

                val qname = QName(qualifiedName.namespaceURI, element.name)


                val retriever = writer.createElementRetriever(initContext, qualifiedName, qname, typeName,parameters)


                val tolerantElement = TolerantElement(qname, tolerantType, assigner, retriever, false)
                typeBuilder.addElement(element.name!!, tolerantElement)
            }
        }

        for (subType in typeBuilder.xsComplexType.getAllConcreteSubtypes()) {
            typeBuilder.addConcreteSubtype(subType)
        }
        complexTypeBuilding.remove(qualifiedName)
        return typeBuilder.build()
    }


    private fun initElements() {
        for (element in xsRegistry.getAllElements()) {

            if (element.type != null) {
                val complexType = tolerantComplexTypes?.get(element.type!!)


                val bindElement = TolerantElement(element.qualifiedName, complexType!!, writer.rootAssigner(element.qualifiedName), NoOpRetriever, false)

                elements[element.qualifiedName] = bindElement
            } else if (element.complexType != null) {

                val complexType = tolerantComplexTypes?.get(element.qualifiedName)

                val bindElement = TolerantElement(element.qualifiedName, complexType!!, writer.rootAssigner(element.qualifiedName), NoOpRetriever, false)

                elements[element.qualifiedName] = bindElement

            }

        }
    }

}


private class ComplexTypeBuilder(
        val name: QName,
        val konstructor: (QName) -> Any,
        val xsComplexType: XsComplexType,
        val proxy: TolerantComplexProxy,
        val closer: Closer
) {

    val elements = ImmutableMap.builder<String, TolerantElement>()!!
    val concreteSubtypes = TolerantMapBuilder<QName>()
    var tolerantComplexType: TolerantComplexType? = null

    var simpleContext: TolerantSimpleContent? = null


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
        var complexType = tolerantComplexType
        if (complexType != null) {
            return complexType
        }

        complexType = TolerantComplexType(name!!, konstructor, elements.build(), concreteSubtypes.build(), simpleContext, closer)
        proxy.delegate = complexType
        tolerantComplexType = complexType
        return complexType
    }
}
