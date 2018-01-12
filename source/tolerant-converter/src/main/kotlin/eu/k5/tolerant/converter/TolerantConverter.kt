package eu.k5.tolerant.converter

import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import eu.k5.tolerantreader.TolerantReader
import eu.k5.tolerantreader.TolerantReaderConfiguration
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.binding.dom.DomWriter
import eu.k5.tolerantreader.tolerant.TolerantSchemaBuilder
import eu.k5.tolerantreader.xs.Schema
import org.w3c.dom.Document
import java.io.StringReader
import java.io.StringWriter
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class TolerantConverter(configuration: TolerantConverterConfiguration) {

    private val name: String = configuration.name!!
    private val reader: TolerantReader
    private val readerConfig: TolerantReaderConfiguration = TolerantReaderConfiguration(configuration.configs)

    init {
        val writer: TolerantWriter = DomWriter()


        val xsRegistry = Schema.parse(configuration.xsd!!)
        xsRegistry.init()
        val tolerantSchema = TolerantSchemaBuilder(xsRegistry, writer).build()
        reader = TolerantReader(tolerantSchema)
    }

    fun convert(request: TolerantConverterRequest): TolerantConverterResult {

        val result = reader.read(createStream(request.content!!), readerConfig)
        if (result is Document) {
            val xmlString = toString(result)
            return TolerantConverterResult(xmlString)
        } else {
            return TolerantConverterResult("invalid result")
        }

    }

    private fun toString(document: Document): String {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        val source = DOMSource(document)

        val resultWriter = StringWriter()
        val result = StreamResult(resultWriter)

        transformer.transform(source, result)

        return resultWriter.toString()
    }


    private fun createStream(xmlContent: String): XMLStreamReader {
        val xmlFactory = XMLInputFactory.newFactory()
        return xmlFactory.createXMLStreamReader(StringReader(xmlContent))
    }

}