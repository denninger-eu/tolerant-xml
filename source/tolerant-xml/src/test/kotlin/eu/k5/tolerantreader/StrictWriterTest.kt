package eu.k5.tolerantreader
import  eu.k5.tr.strict.StrictRoot
import eu.k5.tolerantreader.source.model.XjcRegistry
import eu.k5.tolerantreader.strict.StrictSchemaBuilder
import eu.k5.tolerantreader.xs.ClasspathStreamSource
import eu.k5.tolerantreader.xs.Schema
import org.junit.jupiter.api.Test
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter


class StrictWriterTest {


    @Test
    fun test() {

        val root = StrictRoot()
        root.status = "world"
        root.hello = "echo"



        val writer = Writer()

        initStrictWriter().write(root, writer.xmlWriter)

        print(writer.getOutput())
    }


    private fun getBasePath(): Path {
        return Paths.get("src", "test", "resources", "tolerant-test-models/src/test/resources/xs")
    }

    fun initStrictWriter(): StrictWriter {
        val path = getBasePath().resolve("strict/strict-minimal.xsd")
        val xsRegistry = Schema.parse(path.toString(), ClasspathStreamSource(StrictWriterTest::class.java.classLoader))
        xsRegistry.init()
        val strictSchemaBuilder = StrictSchemaBuilder(XjcRegistry(Arrays.asList(StrictRoot::class.java)), xsRegistry)

        return StrictWriter(strictSchemaBuilder.build())

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