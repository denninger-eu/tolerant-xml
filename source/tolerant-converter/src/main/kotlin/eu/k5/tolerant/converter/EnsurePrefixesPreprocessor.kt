package eu.k5.tolerant.converter

import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class EnsurePrefixesPreprocessor {

    fun ensurePrefixes(xml: String): String {
        val result = AnalysePrefixes.ana(xml)
        if (result.missing.isEmpty()) {
            return xml
        }
        val root = result.document.documentElement
        for (missingPrefix in result.missing) {
            root.setAttribute("xmlns:$missingPrefix", "http://dread/inserted")
        }

        return toString(result.document)
    }

    private fun toString(document: Document): String {
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        val source = DOMSource(document)
        val resultWriter = StringWriter()
        transformer.transform(source, StreamResult(resultWriter))

        return resultWriter.toString().replace("\r\n", "\n")
    }

    companion object {
        private val transformerFactory = TransformerFactory.newInstance()!!

    }
}