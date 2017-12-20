package eu.k5.tolerantreader.xs

import java.nio.file.Path
import javax.xml.bind.JAXBContext
import javax.xml.namespace.QName


class XsRegistry(val initSchema: XsSchema, private val allSchemas: Map<String, XsSchema>) {

    private val complexTypes: MutableMap<QName, XsComplexType> = HashMap()

    private val simpleTypes: MutableMap<QName, XsSimpleType> = HashMap()

    private val elements: MutableMap<QName, XsElement> = HashMap()

    fun init() {

        for (schema in allSchemas.values) {
            for (complexType in schema.complexTypes) {
                complexTypes.put(complexType.getQualifiedName(), complexType)
            }
            for (simpleType in schema.simpleTypes) {
                simpleTypes.put(simpleType.getQualifiedName(), simpleType)
            }
            for (element in schema.elements) {
                elements.put(element.getQualifiedName(), element)
            }
            schema.registry = this
        }

        for (complexType in complexTypes.values) {
            if (!complexType.isAbstract()) {
                var baseComplexType = complexType.getBaseComplexType()
                while (baseComplexType != null) {
                    baseComplexType.addConcreteSubtype(complexType.getQualifiedName())
                    baseComplexType = baseComplexType.getBaseComplexType()
                }
            }
        }
    }

    fun getSimpleType(localName: String): XsSimpleType {

        val first = allSchemas.values.first().simpleTypes.first { n -> localName.equals(n.name) }

        return first
    }

    fun getComplexType(localName: String): XsComplexType {
        // FIXME forward declarationen, performance optimieren

        for (xsSchema in allSchemas.values) {
            for (xsComplexType in xsSchema.complexTypes) {
                if (xsComplexType.localName == localName) {
                    return xsComplexType
                }
            }
        }

        throw IllegalStateException("Unknown complextype with localName: " + localName)

    }

    fun getElement(s: String): XsElement {
        return allSchemas.values.first().elements.first { e -> s.equals(e.localName) }
    }

    fun getAllSimpleTypes(): Collection<XsSimpleType> {
        return simpleTypes.values

    }

    fun getAllComplexTypes(): Collection<XsComplexType> {
        return complexTypes.values
    }

    fun getAllElements(): Collection<XsElement> {
        return elements.values
    }


}