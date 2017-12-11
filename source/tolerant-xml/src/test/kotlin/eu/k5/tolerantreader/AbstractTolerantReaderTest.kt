package eu.k5.tolerantreader

import eu.k5.tolerantreader.tolerant.TolerantSchemaBuilder
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.xs.ClasspathStreamSource
import eu.k5.tolerantreader.xs.Schema
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader

abstract class AbstractTolerantReaderTest {

    fun readSimpleType(request: String): Any? = read(SIMPLE_TYPES + "/" + request, getReader("xml/" + SIMPLE_TYPES + "/simple-types.xsd"))
    fun readComplexType(request: String): Any? = read(COMPLEX_TYPES + "/" + request, getReader("xml/" + COMPLEX_TYPES + "/complex-types.xsd"))
    fun readMinimalType(request: String): Any? = read(MINIMAL + "/" + request, getReader("xml/" + MINIMAL + "/minimal.xsd"))

    fun readModelType(request: String): Any? = read(MODEL + "/" + request, getReader("xs/import.xsd"))


    private fun read(request: String, reader: TolerantReader): Any? {
        val stream = openRequest(request)
        return reader.read(stream)
    }

    private fun openRequest(name: String): XMLStreamReader {
        val path: Path = getBasePath().resolve(name + ".xml")
        val xmlContent = String(Files.readAllBytes(path), StandardCharsets.UTF_8)

        val xmlFactory = XMLInputFactory.newFactory()

        return xmlFactory.createXMLStreamReader(StringReader(xmlContent))
    }


    companion object {
        const val MINIMAL = "minimal"
        const val COMPLEX_TYPES = "complex-types"
        const val SIMPLE_TYPES = "simple-types"
        const val MODEL = "model"
    }

    private fun getBasePath(): Path {
        return Paths.get("src", "test", "resources", "xml")
    }


    abstract fun getReader(path: String): TolerantReader

}

class ReaderCache(val writer: TolerantWriter) {
    private val readers = HashMap<String, TolerantReader>()

    fun getReader(xsPath: String): TolerantReader = readers.computeIfAbsent(xsPath) { createReader(it) }

    private fun createReader(xsdPath: String): TolerantReader {


        val xsRegistry = Schema.parse(xsdPath, ClasspathStreamSource(ReaderCache::class.java.classLoader))
        xsRegistry.init()
        val tolerantSchema = TolerantSchemaBuilder(xsRegistry, writer).build()
        return TolerantReader(tolerantSchema)
    }


}