package eu.k5.tolerantreader

import eu.k5.tolerantreader.reader.TolerantReader
import  eu.k5.tr.strict.StrictRoot
import eu.k5.tolerantreader.source.model.XjcRegistry
import eu.k5.tolerantreader.strict.StrictSchemaBuilder
import eu.k5.tolerantreader.xs.Schema
import eu.k5.tr.model.ListType
import eu.k5.tr.model.inheritance.ComplexInheritance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter


class StrictWriterTest : AbstractTolerantReaderTest() {
    override fun getReader(path: String): TolerantReader = reader.getReader(path)


    @Test
    fun minimal() {
        val root = StrictRoot()
        root.status = "world"
        root.hello = "echo"

        val writer = Writer()
        initStrictWriter().write(root, writer.xmlWriter)
        assertSimilar("minimal", writer)
    }


    @Test
    fun complexTypeInheritance() {
        testModel("complex-type-inheritance")
    }


    @Test
    fun simpleTypeList() {
        testModel("simple-type-list")
    }

    private fun testModel(request: String) {

        val obj = readModelType(request)
                as? Any ?: Assertions.fail<Nothing>("Invalid root type")


        writeModel(obj, request)

    }

    private fun getBasePath(): Path {
        return Paths.get("src", "test", "resources", "/xs")
    }


    private fun writeModel(obj: Any, request: String) {
        val writer = Writer()
        initStrictWriter().write(obj, writer.xmlWriter)
        assertSimilar(request, writer)
    }

    private fun initStrictWriter(): StrictWriter {
        val xsRegistry = Schema.parse("classpath:xs/import.xsd")
        xsRegistry.init()
        val xjcRegistry = XjcRegistry(Arrays.asList(StrictRoot::class.java, ComplexInheritance::class.java, ListType::class.java))
        xjcRegistry.init()
        val strictSchemaBuilder = StrictSchemaBuilder(xjcRegistry, xsRegistry)

        return StrictWriter(strictSchemaBuilder.build())
    }


    private fun assertSimilar(testCase: String, writer: Writer) {

        val output = writer.getOutput()
        print(output)
        val myDiff = DiffBuilder.compare(Input.fromString(output))
                .withTest(Input.fromFile("src/test/resources/refout/strict/" + testCase + ".xml"))
                .checkForSimilar().ignoreWhitespace()
                .build()

        Assertions.assertFalse(myDiff.hasDifferences(), myDiff.toString())
    }
}

class Writer {
    val stringWriter = StringWriter()
    val xmlWriter: XMLStreamWriter

    init {
        val output = XMLOutputFactory.newInstance()
        xmlWriter = output.createXMLStreamWriter(stringWriter)
        xmlWriter.writeStartDocument()
    }

    fun getOutput(): String {
        xmlWriter.flush()
        return stringWriter.toString()
    }


}