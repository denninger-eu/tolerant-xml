package eu.k5.tolerantreader.xs

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import javax.xml.namespace.QName

class XsTest {


    @Test
    @DisplayName("Empty Schema")
    fun emptySchema() {
        val registry = resolve("empty-schema")
        Assertions.assertEquals("http://k5.eu/tr", registry.initSchema.targetNamespace)
    }

    @Test
    @DisplayName("Import Schema")
    fun importSchema() {
        val registry = resolve("import/schema-with-import")
        Assertions.assertEquals("http://k5.eu/tr", registry.initSchema.targetNamespace)

        val imported = registry.initSchema.imports[0].resolvedSchema
        Assertions.assertEquals("http://k5.eu/tr2", imported?.targetNamespace)
    }

    @Test
    @DisplayName("Import Schema cyclic")
    fun importSchemaCyclic() {
        val registry = resolve("import-cyclic/schema-with-import")
        Assertions.assertEquals("http://k5.eu/tr", registry.initSchema.targetNamespace)

        val imported = registry.initSchema.imports[0].resolvedSchema
        Assertions.assertEquals("http://k5.eu/tr2", imported?.targetNamespace)
    }

    @Test
    @DisplayName("Simple type")
    fun simpleType() {
        val registry = resolve("simple-type-enumeration")

        val simpleType = registry.getSimpleType("size")
        Assertions.assertEquals("size", simpleType.name)
        Assertions.assertEquals(QName(XSD_NAMESPACE, "string"), simpleType.restriction?.base)

    }

    @Test
    @DisplayName("Simple type with enumeration")
    fun simpleTypeEnumeration() {
        val registry = resolve("simple-type-enumeration")
        val enumeration = registry.getSimpleType("size").restriction!!.enumeration

        Assertions.assertEquals(5, enumeration.size)
        Assertions.assertEquals("XS", enumeration[0].value)
        Assertions.assertEquals("XL", enumeration[4].value)
    }

    @Test
    @DisplayName("ComplexType with direct sequence no complexContent")
    fun complexType() {
        val registry = resolve("complex-type")
        val complexType = registry.getComplextType("personinfo")

        Assertions.assertNotNull(complexType.sequence)
        Assertions.assertEquals(2, complexType.sequence!!.elements.size)
        Assertions.assertEquals("http://k5.eu/tr", complexType.getQualifiedName().namespaceURI)
        Assertions.assertEquals("personinfo", complexType.getQualifiedName().localPart)

        Assertions.assertEquals(1, complexType.attributes.size)
        Assertions.assertEquals("attrib", complexType.attributes[0].name)

        val element = complexType.sequence!!.elements.get(0)

        Assertions.assertEquals("firstname", element.name)
        Assertions.assertEquals(QName(XSD_NAMESPACE, "string"), element.type)

        val declaredElements = complexType.getDeclaredElements()
        Assertions.assertEquals(2, declaredElements.size)
    }


    @Test
    @DisplayName("ComplexType with complexContent extension")
    fun complexTypeWithComplexContent() {
        val registry = resolve("complex-type")
        val complexType = registry.getComplextType("fullpersoninfo")

        Assertions.assertNull(complexType.sequence)

        val elements = complexType.getDeclaredElements()

        Assertions.assertEquals(3, elements.size)

        val element = elements[0]

        Assertions.assertEquals("address", element.name)
        Assertions.assertEquals(QName(XSD_NAMESPACE, "string"), element.type)
    }


    @Test
    @DisplayName("ComplexType with simpleContent")
    fun complexTypeSimpleContent() {
        val registry = resolve("complex-type")
        val complexType = registry.getComplextType("fullpersoninfo")

        Assertions.assertNull(complexType.sequence)

        val elements = complexType.getDeclaredElements()

        Assertions.assertEquals(3, elements.size)

        val element = elements[0]

        Assertions.assertEquals("address", element.name)
        Assertions.assertEquals(QName(XSD_NAMESPACE, "string"), element.type)
    }

    @Test
    @DisplayName("Element")
    fun element() {
        val registry = resolve("element")
        val element = registry.getElement("order")

        Assertions.assertEquals("order", element.localName)
        Assertions.assertEquals("ordertype", element.type?.localPart)
    }

    @Test
    @DisplayName("Element")
    fun elementAnonymousComplexType() {
        val registry = resolve("element-anonymous-complextype")
        val element = registry.getElement("order")

        Assertions.assertEquals("order", element.localName)
        Assertions.assertEquals("order", element.complexType!!.getQualifiedName().localPart)
    }

    private fun resolve(fileName: String): XsRegistry {
        val file = Paths.get("src", "test", "resources", "tolerant-test-models/src/test/resources/xs", fileName + ".xsd")

        val source = ClasspathStreamSource(XsTest::class.java.classLoader)

        return Schema.parse("xs/" + fileName + ".xsd", source)
    }


}