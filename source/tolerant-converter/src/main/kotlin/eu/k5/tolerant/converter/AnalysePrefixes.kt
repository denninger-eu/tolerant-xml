package eu.k5.tolerant.converter

import com.google.common.collect.Multimap
import com.google.common.collect.TreeMultimap
import org.w3c.dom.*
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.HashSet

object AnalysePrefixes {

    data class Prefixes(
            val declared: Set<String>,
            val used: Set<String>
    )

    fun ana(xml: String): Prefixes {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        documentBuilderFactory.isNamespaceAware = false
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(InputSource(StringReader(xml)))

        return ana(document.documentElement)
    }

    fun ana(element: Element): Prefixes {
        val context = AnalyseContext()
        analyseElement(context, element)



        return Prefixes(declared = Collections.unmodifiableSet(context.getAllDeclared()),
                used = Collections.unmodifiableSet(context.getAllUsed()))
    }

    private fun analyseElement(context: AnalyseContext, element: Element) {
        val tagName = element.tagName
        if (tagName?.contains(":") == true) {
            val delimiter = tagName.indexOf(":")
        }

        analyseAttributes(context, element.attributes)

        val children = element.childNodes
        for (index in 0 until children.length) {
            analyse(context, children.item(index))
        }
    }

    private fun analyseAttributes(context: AnalyseContext, attributes: NamedNodeMap) {
        for (index in 0 until attributes.length) {
            val attribute = attributes.item(index) as Attr
            val name = attribute.name

            val delimiter = name.indexOf(':')
            if (delimiter >= 0) {
                val prefix = name.substring(0, delimiter)
                if (prefix == "xmlns") {
                    context.addDeclared(name.substring(delimiter+1))
                } else {
                    context.addUsed(prefix)
                }
            }
            val value = attribute.nodeValue
            if (value.contains(":")) {

            }
        }

    }

    private fun analyse(context: AnalyseContext, node: Node) {
        if (node is Element) {
            analyseElement(context, node)
        } else if (node is Text) {
            // do nothing
        } else if (node is Comment) {
            // do nothing
        } else {
            TODO("Add other types")
        }

    }

    private class AnalyseContext {
        val declared: Multimap<Int, String> = TreeMultimap.create()
        val used: Multimap<Int, String> = TreeMultimap.create()
        var level = 0

        fun push() {
            level++
        }

        fun pop() {
            level--

        }

        fun getAllUsed(): Set<String> {
            return HashSet()
        }

        fun getAllDeclared(): Set<String> {
            return HashSet()
        }

        fun addUsed(prefix: String) {
            used.put(level, prefix)

        }

        fun addDeclared(prefix: String) {
            declared.put(level, prefix)
        }
    }
}