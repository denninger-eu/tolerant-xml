package eu.k5.tolerant.converter

import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import eu.k5.tolerantreader.TolerantReader
import eu.k5.tolerantreader.TolerantReaderConfiguration
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.binding.dom.DomWriter
import eu.k5.tolerantreader.tolerant.TolerantSchemaBuilder
import eu.k5.tolerantreader.xs.Schema
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream
import java.io.PrintWriter
import java.io.StringReader
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants.NODESET


class TolerantConverter(configuration: TolerantConverterConfiguration) {

    private val reader: TolerantReader
    private val readerConfig: TolerantReaderConfiguration = TolerantReaderConfiguration(configuration.configs)
    private val documentBuilderFactory = DocumentBuilderFactory.newInstance()

    init {
        val writer: TolerantWriter = DomWriter()


        val xsRegistry = Schema.parse(configuration.xsd!!.trim())
        xsRegistry.init()
        val tolerantSchema = TolerantSchemaBuilder(xsRegistry, writer).build()
        reader = TolerantReader(tolerantSchema)
    }

    fun convert(request: TolerantConverterRequest): TolerantConverterResult {

        try {

//            val extracted = extract(request)

            val result = reader.read(createStream(request.content!!), readerConfig)

            if (result is Document) {
                val xmlString = beautify(result)
                return TolerantConverterResult(content = xmlString)
            } else {
                return TolerantConverterResult(error = "Root Element could not be interpreted")
            }
        } catch (exception: Exception) {
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            exception.printStackTrace(printWriter)
            printWriter.flush()
            return TolerantConverterResult(error = stringWriter.toString())
        }

    }


    private fun extract(request: TolerantConverterRequest): String {
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(InputSource(StringReader(request.content!!)))

        val xPath = XPathFactory.newInstance().newXPath()
        val nodes = xPath.evaluate("/*[local-name()='Envelope']/*[local-name()='Body']/*", document.documentElement, XPathConstants.NODESET)

        if (nodes is NodeList) {
            println(nodes.length)

            if (nodes.length == 1) {
                val element = nodes.item(0) as Element
                return toString(element)
            }
        } else {


        }
        return ""
    }


    private fun beautify(document: Document): String {

        val asString = toString(document.documentElement)


        val dBuilder = documentBuilderFactory.newDocumentBuilder()

        val doc = dBuilder.parse(ByteArrayInputStream(asString.toByteArray(StandardCharsets.UTF_8)))
        return toString(doc.documentElement)
    }

    private fun toString(document: Element): String {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        val source = DOMSource(document)
        val resultWriter = StringWriter()

        transformer.transform(source, StreamResult(resultWriter))

        return resultWriter.toString()
    }


    private fun createStream(xmlContent: String): XMLStreamReader {
        val xmlFactory = XMLInputFactory.newFactory()
        return xmlFactory.createXMLStreamReader(StringReader(xmlContent))
    }

}