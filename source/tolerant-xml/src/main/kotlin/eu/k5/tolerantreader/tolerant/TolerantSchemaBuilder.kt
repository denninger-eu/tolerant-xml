package eu.k5.tolerantreader.tolerant

import com.google.common.collect.ImmutableMap
import eu.k5.tolerantreader.*
import eu.k5.tolerantreader.binding.Closer
import eu.k5.tolerantreader.binding.ElementParameters
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.xs.XsComplexType
import eu.k5.tolerantreader.xs.XsRegistry
import javax.xml.namespace.QName

class TolerantSchemaBuilder(private val xsRegistry: XsRegistry, private val writer: TolerantWriter) {


    private val simpleTypes: MutableMap<QName, TolerantSimpleType> = HashMap()


    private val complexTypes: MutableMap<QName, TolerantComplexType> = HashMap()

    private val complexTypeBuilders: MutableMap<QName, ComplexTypeBuilder> = HashMap()

    private val complexTypeBuilding: MutableSet<QName> = HashSet()

    private val elements: MutableMap<QName, TolerantElement> = HashMap()

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
        TODO("add others: " + name)
    }

    fun build(): TolerantSchema {

        initBaseTypes()

        initSimpleTypes()

        prepareComplexTypes()

        finishComplexTypes()

        initElements()



        return TolerantSchema(TolerantMap.of(elements.values) { it.qname }, tolerantComplexTypes!!, writer)
    }

    private fun initBaseTypes() {
        simpleTypes.put(xsString, TolerantStringType(xsString))
        simpleTypes.put(xsAnyUri, TolerantStringType(xsAnyUri))
        simpleTypes.put(xsBase64Binary, TolerantBase64BinaryType())
        simpleTypes.put(xsHexBinary, TolerantHexBinaryType())
        simpleTypes.put(xsBoolean, TolerantBooleanType())
        simpleTypes.put(xsQname, TolerantQNameType())


        simpleTypes.put(xsDate, TolerantTemporalType(xsDate))
        simpleTypes.put(xsDatetime, TolerantTemporalType(xsDatetime))
        simpleTypes.put(xsDuration, TolerantTemporalType(xsDuration))
        simpleTypes.put(xsGDay, TolerantTemporalType(xsGDay))
        simpleTypes.put(xsGMonth, TolerantTemporalType(xsGMonth))
        simpleTypes.put(xsGMonthDay, TolerantTemporalType(xsGMonthDay))
        simpleTypes.put(xsGYear, TolerantTemporalType(xsGYear))
        simpleTypes.put(xsGYearMonth, TolerantTemporalType(xsGYearMonth))
        simpleTypes.put(xsTime, TolerantTemporalType(xsTime))

        simpleTypes.put(xsByte, byteType)
        simpleTypes.put(xsDecimal, decimalType)
        simpleTypes.put(xsInt, intType)
        simpleTypes.put(xsInteger, integerType)
        simpleTypes.put(xsLong, longType)
        simpleTypes.put(xsNegativeInteger, negativeIntegerType)
        simpleTypes.put(xsNonNegativeInteger, nonNegativeIntegerType)
        simpleTypes.put(xsNonPositiveInteger, nonPositiveIntegerType)
        simpleTypes.put(xsPositiveInteger, positiveIntegerType)
        simpleTypes.put(xsShort, shortType)
        simpleTypes.put(xsUnsignedLong, unsignedLongType)
        simpleTypes.put(xsUnsignedInt, unsignedIntType)
        simpleTypes.put(xsUnsignedShort, unsignedShortType)
        simpleTypes.put(xsUnsignedByte, unsignedByteType)
        simpleTypes.put(xsDouble, doubleType)
        simpleTypes.put(xsFloat, floatType)

        simpleTypes.put(xsId, TolerantIdType())
        simpleTypes.put(xsIdRef, TolerantIdRefType())
    }


    private fun initSimpleTypes() {
        for (simpleType in xsRegistry.getAllSimpleTypes()) {

            val base = simpleType.restriction?.base

            if (simpleType.isEnum()) {

                val literals = simpleType.enumLiterals()

                val enumSupplier = writer.createEnumSupplier(initContext, simpleType.getQualifiedName(), literals)
                simpleTypes.put(simpleType.getQualifiedName(), TolerantEnumType(simpleType.getQualifiedName(), enumSupplier))


            } else {

                val baseType = simpleTypes[simpleType.restriction!!.base!!]

                if (baseType != null) {
                    val type = TolerantSimpleRestriction(simpleType.getQualifiedName(), baseType)
                    simpleTypes.put(type.getQualifiedName(), type)
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
        val qname = xsComplexType.getQualifiedName()
        val createSupplier = writer.createSupplier(initContext, qname)
        var builder = ComplexTypeBuilder(qname, createSupplier, xsComplexType, TolerantComplexProxy(qname), writer.createCloser(initContext))
        complexTypeBuilders[xsComplexType.getQualifiedName()] = builder

        val simpleContent = xsComplexType.simpleContent
        if (simpleContent != null) {
            val baseName = simpleContent.extension?.base

            val baseType = simpleTypes.get(baseName)

            val parameters = ElementParameters(false, 0, false)

            val elementName = QName(XSD_NAMESPACE, "value")

            val assigner = writer.createElementAssigner(initContext, qname, elementName, baseType!!.getTypeName(), parameters)

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
                val assigner = writer.createElementAssigner(initContext, qualifiedName, QName(qualifiedName.namespaceURI, attribute.name!!), simpleType.getTypeName(), parameters)

                val qname = QName(qualifiedName.namespaceURI, attribute.name)

                typeBuilder.addElement(attribute.name!!, TolerantElement(qname, simpleType, assigner, true))
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

                val assigner = writer.createElementAssigner(initContext, qualifiedName, element.getQualifiedName(), tolerantType.getTypeName(), parameters)

                val qname = QName(qualifiedName.namespaceURI, element.name)


                val tolerantElement = TolerantElement(qname, tolerantType, assigner, false)
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


                val bindElement = TolerantElement(element.getQualifiedName(), complexType!!, writer.rootAssigner(element.getQualifiedName()), false)

                elements.put(element.getQualifiedName(), bindElement)
            } else if (element.complexType != null) {

                val complexType = tolerantComplexTypes?.get(element.getQualifiedName())

                val bindElement = TolerantElement(element.getQualifiedName(), complexType!!, writer.rootAssigner(element.getQualifiedName()), false)

                elements.put(element.getQualifiedName(), bindElement)

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
