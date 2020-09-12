package eu.k5.tolerantreader.xs

import eu.k5.tolerantreader.XSD_NAMESPACE
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
        val registry = resolve("simple-type-enumeration").init()

        val simpleType = registry.getSimpleType("sizeEnum")
        Assertions.assertEquals("sizeEnum", simpleType.name)
        Assertions.assertEquals(QName(XSD_NAMESPACE, "string"), simpleType.restriction?.base)

    }

    @Test
    @DisplayName("Simple type with enumeration")
    fun simpleTypeEnumeration() {
        val registry = resolve("simple-type-enumeration").init()
        val enumeration = registry.getSimpleType("sizeEnum").restriction!!.enumeration

        Assertions.assertEquals(5, enumeration?.size)
        Assertions.assertEquals("XS", enumeration!![0].value)
        Assertions.assertEquals("XL", enumeration!![4].value)
    }

    @Test
    @DisplayName("ComplexType with direct sequence no complexContent")
    fun complexType() {
        val registry = resolve("complex-type").init()
        val complexType = registry.getComplexTypeByLocalName("personinfo")

        Assertions.assertNotNull(complexType.sequence)
        Assertions.assertEquals(2, complexType.sequence!!.elements.size)
        Assertions.assertEquals("http://k5.eu/tr/model", complexType.qualifiedName.namespaceURI)
        Assertions.assertEquals("personinfo", complexType.qualifiedName.localPart)

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
        val registry = resolve("complex-type").init()
        val complexType = registry.getComplexTypeByLocalName("fullpersoninfo")

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
        val registry = resolve("complex-type").init()
        val complexType = registry.getComplexTypeByLocalName("fullpersoninfo")

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
        val registry = resolve("element").init()
        val element = registry.getElement("order2")

        Assertions.assertEquals("order2", element.localName)
        Assertions.assertEquals("ordertype2", element.type?.localPart)
    }

    @Test
    @DisplayName("Element")
    fun elementAnonymousComplexType() {
        val registry = resolve("element-anonymous-complextype").init()
        val element = registry.getElement("ordercanon")

        Assertions.assertEquals("ordercanon", element.localName)
        Assertions.assertEquals("ordercanon", element.complexType!!.qualifiedName.localPart)
    }

    private fun resolve(fileName: String): XsRegistry {

        val zip = XsTest::class.java.classLoader.getResourceAsStream("xs.zip")

        //val source = ClasspathStreamSource(XsTest::class.java.classLoader)

        val source = ZipStreamSource(zip)

        return Schema.parse("xs/$fileName.xsd", source)
    }


}