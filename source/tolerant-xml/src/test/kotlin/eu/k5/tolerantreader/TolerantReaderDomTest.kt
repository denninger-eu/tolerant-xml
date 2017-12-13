package eu.k5.tolerantreader

import eu.k5.tolerantreader.binding.dom.DomWriter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input


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
    @DisplayName("Read complex type inherited")
    fun readComplexTypesInherited() {
        testComplex("complex-types-inherited")
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

    private fun testMinimal(testCase: String) {
        val obj = readMinimalType(testCase)
                as? Document ?: Assertions.fail<Nothing>("Invalid root type")

        toString(obj)

        assertSimilar(testCase, obj)
    }


    private fun testComplex(testCase: String) {
        val obj = readComplexType(testCase)
                as? Document ?: Assertions.fail<Nothing>("Invalid root type")

        toString(obj)

        assertSimilar(testCase, obj)
    }

    private fun testModel(testCase: String) {
        val obj = readModelType(testCase)
                as? Document ?: Assertions.fail<Nothing>("Invalid root type")

        toString(obj)

        assertSimilar(testCase, obj)
    }

    private fun assertSimilar(testCase: String, document: Document) {

        val myDiff = DiffBuilder.compare(Input.fromDocument(document))
                .withTest(Input.fromFile("src/test/resources/refout/dom/" + testCase + ".xml"))
                .checkForSimilar().ignoreWhitespace()
                .build()

        Assertions.assertFalse(myDiff.hasDifferences(), myDiff.toString())
    }

    private fun toString(document: Document) {

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(document)

        val resultWriter = StringWriter()
        val result = StreamResult(resultWriter)

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result)

        print(resultWriter.toString())
    }


    override fun getReader(path: String): TolerantReader = reader.getReader(path)

    companion object {
        val reader = ReaderCache(DomWriter())
    }


}