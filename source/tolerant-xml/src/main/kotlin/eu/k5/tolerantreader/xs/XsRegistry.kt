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

    fun getComplexTypeByLocalName(localName: String): XsComplexType {
        for ((qName, type) in complexTypes) {
            if (qName.localPart == localName) {
                return type;
            }
        }

        throw IllegalStateException("Unknown complextype with localName: " + localName)

    }

    fun getComplexType(name: QName): XsComplexType? {
        // FIXME forward declarationen, performance optimieren

        return complexTypes[name]


        /*    for (xsSchema in allSchemas.values) {

                xsSchema.complexTypes[name]
                for (xsComplexType in xsSchema.complexTypes) {
                    if (xsComplexType.getQualifiedName() == name) {
                        return xsComplexType
                    }
                }
            }

            throw IllegalStateException("Unknown complextype with localName: " + localName)
    */
    }

    fun getElement(s: String): XsElement {
        return elements.values.first { s.equals(it.localName) }
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

    fun writeDebugOut() {
        println("ComplexTypes")
        println(complexTypes)
        println("SimpleTypes")
        println(simpleTypes)
        println("Elements")
        println(elements)

    }

}