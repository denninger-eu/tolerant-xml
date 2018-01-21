package eu.k5.tolerantreader

import eu.k5.tolerantreader.tolerant.TolerantSchemaBuilder
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.binding.model.Binder
import eu.k5.tolerantreader.binding.model.PackageMapping
import eu.k5.tolerantreader.binding.model.PackageMappingBuilder
import eu.k5.tolerantreader.reader.TolerantReader
import eu.k5.tolerantreader.reader.TolerantReaderConfiguration
import eu.k5.tolerantreader.transformer.Transformer
import eu.k5.tolerantreader.transformer.Transformers
import eu.k5.tolerantreader.xs.ClasspathStreamSource
import eu.k5.tolerantreader.xs.Schema
import eu.k5.tr.model.NumericTypes
import eu.k5.tr.model.idref.Referenced
import eu.k5.tr.model.inheritance.ComplexInheritance
import eu.k5.tr.model.inheritance.basis.NsBaseType
import eu.k5.tr.model.inheritance.sub.NsComplexInheritance
import eu.k5.tr.strict.StrictRoot
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader

abstract class AbstractTolerantReaderTest {

    fun readSimpleType(request: String): Any? = read(SIMPLE_TYPES + "/" + request, getReader("xml/" + SIMPLE_TYPES + "/simple-types.xsd"))

    fun readComplexType(request: String): Any? = read(COMPLEX_TYPES + "/" + request, getReader("xml/" + COMPLEX_TYPES + "/complex-types.xsd"))

    fun readMinimalType(request: String): Any? = read(MINIMAL + "/" + request, getReader("xml/" + MINIMAL + "/minimal.xsd"))

    fun readModelType(request: String): Any? = read(MODEL + "/" + request, getReader("xs/import.xsd"))


    private fun read(request: String, reader: TolerantReader): Any? {
        val stream = openRequest(request)
        return reader.read(stream, TolerantReaderConfiguration(HashMap())).instance
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

        val reader = ReaderCache(Binder(packageMapping()))

        private fun packageMapping(): PackageMapping {
            val builder = PackageMappingBuilder()
            builder.add("http://k5.eu/tr/minimal", "model.minimal")
            builder.add("http://k5.eu/tr/complex", "model.complex")

            builder.add("http://k5.eu/tr/strict", StrictRoot::class.java.`package`.name)
            builder.add("http://k5.eu/tr/model", NumericTypes::class.java.`package`.name)
            builder.add("http://k5.eu/tr/model/idref", Referenced::class.java.`package`.name)
            builder.add("http://k5.eu/tr/model/inheritance", ComplexInheritance::class.java.`package`.name)
            builder.add("http://k5.eu/tr/model/inheritance/sub", NsComplexInheritance::class.java.`package`.name)
            builder.add("http://k5.eu/tr/model/inheritance/basis", NsBaseType::class.java.`package`.name)
            return builder.build()
        }
    }

    private fun getBasePath(): Path = Paths.get("src", "test", "resources", "xml")


    abstract fun getReader(path: String): TolerantReader

}

class ReaderCache(private val writer: TolerantWriter) {
    private val readers = HashMap<String, TolerantReader>()

    fun getReader(xsPath: String): TolerantReader = readers.computeIfAbsent(xsPath) { createReader(it) }

    private fun createReader(xsdPath: String): TolerantReader {

        val xsRegistry = Schema.parse(xsdPath, ClasspathStreamSource(ReaderCache::class.java.classLoader))
        xsRegistry.init()
        val tolerantSchema = TolerantSchemaBuilder(InitContext(), xsRegistry, writer, transformers = createTransformers()).build()
        return TolerantReader(tolerantSchema)
    }


    private fun createTransformers(): Transformers {
        val transformers = Transformers()

        transformers.transformers.add(Transformer(type = "SubType", element = "subElementRename", target = "subElement"))
        transformers.transformers.add(Transformer(type = "FullPerson", element = "InfoRename", target = "Info"))
        transformers.transformers.add(Transformer(type = "FullPerson", element = "name", target = "Info/firstname"))
        transformers.transformers.add(Transformer(type = "NsComplexInheritance", element = "typeRename", target = "type"))

        return transformers
    }

}