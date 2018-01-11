package eu.k5.tolerantreader

import eu.k5.tolerantreader.binding.dom.DomWriter
import eu.k5.tr.model.ListType
import eu.k5.tr.model.idref.Reference
import eu.k5.tr.model.inheritance.ComplexInheritance
import eu.k5.tr.model.inheritance.SubType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import javax.xml.transform.OutputKeys


class TolerantReaderDomTest : AbstractTolerantReaderTest() {

    @Test
    @DisplayName("Read minimal xml")
    fun readMinimal() {
        testMinimal("minimal")
    }

    @Test
    @DisplayName("Read minimal with attributes")
    fun readMinimalAttributes() {
        testMinimal("minimal-attributes")
    }


    @Test
    @DisplayName("Read minimal with list")
    fun readMinimalList() {
        testMinimal("minimal-with-list-elements")
    }

    @Test
    @DisplayName("Read complex type")
    fun readComplexTypes() {
        testComplex("complex-types")
    }


    @Test
    @DisplayName("Read complex type cyclic types")
    fun readComplexTypesCyclic() {
        testComplex("complex-types-cyclic")
    }

    @Test
    fun readSimpleTypesNumeric() {
        testModel("simple-type-numeric")
    }

    @Test
    @DisplayName("Read model type. idref")
    fun readModelIdref() {
        testModel("complex-type-idref")
    }

    @Test
    @DisplayName("Read model type. idref attribute")
    fun readModelIdrefAttribute() {
        testModel("complex-type-idref-attribute")
    }


    @Test
    @DisplayName("Read model type. idref forward")
    fun readModelIdrefForward() {
        testModel("complex-type-idref-forward")
    }

    @Test
    @DisplayName("Read model type. list")
    fun readModelList() {
        testModel("simple-type-list")
    }

    @Test
    @DisplayName("Read model type. enum")
    fun readModelEnum() {
        testModel("simple-type-enum")
    }


    @Test
    @DisplayName("Read model type. enum invalid")
    fun readModelEnumInvalid() {
        testModel("simple-type-enum-invalid")
    }

    @Test
    @DisplayName("Read model type. Inheritance")
    fun readComplexTypesInheritance() {
        testModel("complex-type-inheritance")
    }


    @Test
    @DisplayName("Read model type. Inheritance different ns")
    fun readComplexTypesInheritanceNs() {
        testModel("complex-type-inheritance-ns")
    }

    @Test
    @DisplayName("Read simple type. comments")
    fun readSimpleComments() {
        testMinimal("simple-types-comments")
    }

    @Test
    @DisplayName("Read complex type. comments")
    fun readModelComments() {
        testModel("complex-type-comment")
    }


    private fun testMinimal(testCase: String) {
        val obj = readMinimalType(testCase)
                as? Document ?: Assertions.fail<Nothing>("Invalid root type")

        val xmlString = toString(obj)
        println(xmlString)

        assertSimilar(testCase, xmlString)
    }


    private fun testComplex(testCase: String) {
        val obj = readComplexType(testCase)
                as? Document ?: Assertions.fail<Nothing>("Invalid root type")

        val xmlString = toString(obj)
        println(xmlString)

        assertSimilar(testCase, xmlString)
    }

    private fun testModel(testCase: String) {
        val obj = readModelType(testCase)
                as? Document ?: Assertions.fail<Nothing>("Invalid root type")

        val xmlString = toString(obj)

        println(xmlString)

        assertSimilar(testCase, xmlString)
    }

    private fun assertSimilar(testCase: String, document: String) {

        val myDiff = DiffBuilder.compare(Input.fromString(document))
                .withTest(Input.fromFile("src/test/resources/refout/dom/$testCase.xml"))
                .checkForSimilar().ignoreWhitespace()
                .build()

        Assertions.assertFalse(myDiff.hasDifferences(), myDiff.toString())
    }

    private fun toString(document: Document): String {

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()

        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        val source = DOMSource(document)

        val resultWriter = StringWriter()
        val result = StreamResult(resultWriter)

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result)

        return resultWriter.toString()
    }


    override fun getReader(path: String): TolerantReader = reader.getReader(path)

    companion object {
        val reader = ReaderCache(DomWriter())
    }


}