package eu.k5.tolerant.converter

import eu.k5.tolerant.converter.config.Explicit
import eu.k5.tolerant.converter.config.Pattern
import eu.k5.tolerantreader.binding.dom.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class NamespaceBeautifierTest {

    @Test
    fun noNamespace() {
        val result = beautify("<empty/>")
        assertSimilar("""<empty/>""", result)
    }

    @Test
    fun noNamespaceTextContent() {
        val result = beautify("<empty>text</empty>")
        assertSimilar("""<empty>text</empty>""", result)
    }

    @Test
    fun noNamespaceWithComment() {
        val result = beautify("<empty><!--comment--></empty>")
        assertSimilar("""<empty>
|<!--comment-->
|</empty>""".trimMargin(), result)
    }

    @Test
    fun noNamespaceWithAttribute() {
        val result = beautify("<empty attribute=\"value\"/>")
        assertSimilar("""<empty attribute="value"/>""", result)
    }


    @Test
    fun explicitPrefix_usesPrefix() {
        val result = beautify("""<ns:env xmlns:ns="http://exp" />""", explicitStrategy())
        assertSimilar("""<exp:env xmlns:exp="http://exp"/>""", result)
    }

    @Test
    fun explicitAttributeNamespace_usesPrefix() {
        val result = beautify("""<ns:env xmlns:ns="http://exp" ns:attribute="value" />""", explicitStrategy())
        assertSimilar("""<exp:env xmlns:exp="http://exp" exp:attribute="value"/>""", result)
    }

    @Test
    fun explicitAttributeValueWithNamespace_usesPrefix() {
        val result = beautify("<empty type=\"ns:value\" xmlns:ns=\"http://exp\" />", explicitStrategy())
        assertSimilar("""<empty xmlns:exp="http://exp" type="exp:value"/>""", result)
    }

    @Test
    fun explicit_nestedElements_usesPrefix() {
        val result = beautify("""<ns:env xmlns:ns="http://exp" ><ns:nested /></ns:env>""", explicitStrategy())
        assertSimilar("""<exp:env xmlns:exp="http://exp">
<exp:nested/>
</exp:env>""", result)
    }

    @Test
    fun pattern() {
        val strategy = patternStrategy(Pattern(".*", "http://(?<prefix>.*)"))
        val result = beautify("""<ns:env xmlns:ns="http://exp" />""", strategy)
        assertSimilar("""<exp:env xmlns:exp="http://exp"/>""", result)
    }


    @Test
    fun oneSection_allNamespaceAtRoot() {
        val result = beautify("""<ns><ns:one xmlns:ns="http://exp" /><ns:two xmlns:ns="http://exp" /></ns>""", explicitStrategy())
        assertSimilar("""<ns xmlns:exp="http://exp">
<exp:one/>
<exp:two/>
</ns>""", result)
    }

    @Test
    fun twoSection_allNamespaceAtRoot() {
        val strategy = explicitStrategy()

        var sections = listOf("ns.one", "ns.two")
        val result = beautify("""<ns><ns:one xmlns:ns="http://exp" /><ns:two xmlns:ns="http://exp" /></ns>""", strategy, sections)
        assertSimilar("""<ns>
<exp:one xmlns:exp="http://exp"/>
<exp:two xmlns:exp="http://exp"/>
</ns>""", result)
    }

    private fun assertSimilar(expected: String, actual: String) {
        if (sanitized(expected) != sanitized(actual)) {
            println(sanitized(actual))
            assertEquals(expected, actual)
        }
    }

    private fun sanitized(string: String): String {
        val sanitized = string.lines().map { it.trim() }.filter { !it.isBlank() }.filter { it != "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" }.joinToString("\n")

        return sanitized
    }

    private fun beautify(xml: String, strategy: NamespaceStrategy = DefaultNamespaceStrategy, sections: List<String> = ArrayList<String>()): String {

        val documentBuilder = dbf.newDocumentBuilder()
        val document = documentBuilder.parse(xml.byteInputStream(StandardCharsets.UTF_8))
        val beautifier = NamespaceBeautifier(strategy, sections)
        return toString(beautifier.beautify(document))
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

    private fun explicitStrategy(): NamespaceStrategy {
        val config = NamespaceStrategyConfiguration()
        config.explicit[explicit.namespace!!] = explicit.prefix!!
        return ConfigurableNamespaceStrategy(config)
    }


    private fun patternStrategy(pattern: Pattern): NamespaceStrategy {
        val config = NamespaceStrategyConfiguration()
        config.pattern.add(Conditional(pattern.use!!, pattern.extract!!))
        return ConfigurableNamespaceStrategy(config)
    }

    companion object {
        private val dbf = DocumentBuilderFactory.newInstance()!!
        private val transformerFactory = TransformerFactory.newInstance()!!
        private val explicit = Explicit("http://exp", "exp")

        init {
            dbf.isNamespaceAware = true
        }
    }
}