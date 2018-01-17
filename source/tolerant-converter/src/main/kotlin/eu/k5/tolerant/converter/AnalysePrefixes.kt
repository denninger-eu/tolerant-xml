package eu.k5.tolerant.converter

import com.google.common.collect.TreeMultimap
import org.w3c.dom.*
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.HashMap
import kotlin.collections.HashSet

object AnalysePrefixes {

    data class Prefixes(
            var missing: Set<String>,
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
        analyse(context, element)

        val missing = context.getMissing()

        return Prefixes(missing = missing, declared = Collections.unmodifiableSet(context.getAllDeclared()),
                used = Collections.unmodifiableSet(context.getAllUsed()))
    }

    private fun analyseElement(context: AnalyseContext, element: Element) {
        val tagName = element.tagName
        if (tagName?.contains(":") == true) {
            val delimiter = tagName.indexOf(":")
            if (delimiter > 0) {
                context.addUsed(tagName.substring(0, delimiter))
            }
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
                    context.addDeclared(name.substring(delimiter + 1))
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
        context.push()
        if (node is Element) {
            analyseElement(context, node)
        } else if (node is Text) {
            // do nothing
        } else if (node is Comment) {
            // do nothing
        } else {
            TODO("Add other types")
        }
        context.pop()
    }

    private class AnalyseContext {
        private val allDeclared = HashSet<String>()
        private val allUsed = HashSet<String>()

        val declared: TreeMultimap<Int, String> = TreeMultimap.create()
        val used: MutableMap<String, Int> = HashMap()
        var level = 0

        fun push() {
            level++
        }

        fun pop() {
            val undeclared = declared[level]
            for (undeclare in undeclared) {
                val use = used[undeclare]
                if (use != null && use >= level) {
                    used.remove(undeclare)
                }
            }
            level--
        }

        fun getMissing(): Set<String> {
            if (level == 0) {
                return HashSet(used.keys)
            } else {
                throw IllegalStateException("Unbalanced context")
            }
        }

        fun getAllUsed(): Set<String>
                = allUsed


        fun getAllDeclared(): Set<String>
                = allDeclared

        fun addUsed(prefix: String) {
            allUsed.add(prefix)
            used.putIfAbsent(prefix, level)
        }

        fun addDeclared(prefix: String) {
            allDeclared.add(prefix)
            declared.put(level, prefix)
        }
    }
}