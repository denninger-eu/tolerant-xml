package eu.k5.tolerant.converter

import eu.k5.tolerant.converter.config.Explicit
import eu.k5.tolerantreader.binding.dom.ConfigurableNamespaceStrategy
import eu.k5.tolerantreader.binding.dom.NamespaceStrategy
import org.w3c.dom.*
import java.lang.IllegalStateException
import javax.xml.parsers.DocumentBuilderFactory

class NamespaceBeautifier(
        private val namespaceStrategy: NamespaceStrategy,
        private val sections: List<String>

) {
    private val document = dbf.newDocumentBuilder().newDocument()

    private val path = ArrayDeque<String>()
    private val global = HashMap<String, String>()

    private val section = ArrayDeque<NamespaceContext>()


    private fun getNamespacePrefix(namespace: String): String {
        return section.last().getOrCreateNamespace(namespace)
    }


    fun beautify(input: Document): Document {
        val documentElement = document.documentElement
        document.appendChild(handleElement(input.documentElement))
        return document
    }

    private fun handle(node: Node): Node {
        return if (node is Element) {
            handleElement(node)
        } else if (node is Comment) {
            handleComment(node)
        } else if (node is Text) {
            handleText(node)
        } else {
            TODO("unsupported " + node.javaClass.name)
        }

    }

    private fun handleElement(element: Element): Element {
        path.addLast(element.localName)
        if (isSection()) {
            section.addLast(NamespaceContext(global, namespaceStrategy, section.lastOrNull()))
        }

        val newElement = if (element.namespaceURI.isNullOrEmpty()) {
            document.createElement(element.tagName)
        } else {
            val prefix = getNamespacePrefix(element.namespaceURI)
            document.createElementNS(element.namespaceURI, prefix + ":" + element.localName)
        }

        for (index in 0 until element.attributes.length) {
            val attribute = element.attributes.item(index)

            val content = if (attribute.localName != "type") {
                attribute.textContent
            } else if (!attribute.textContent.contains(':')) {
                attribute.textContent
            } else {
                val valuePrefix = attribute.textContent.substring(0, attribute.textContent.indexOf(':'))
                val valueNamespaceUri = attribute.lookupNamespaceURI(valuePrefix)
                if (valueNamespaceUri != null) {
                    /*                if (newElement.lookupNamespaceURI(valueNamespaceUri) == null) {
                                        newElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + getNamespacePrefix(valueNamespaceUri), valueNamespaceUri)

                                    }*/
                    getNamespacePrefix(valueNamespaceUri) + ":" + attribute.textContent.substring(attribute.textContent.indexOf(':') + 1)

                } else {
                    attribute.textContent
                }
            }

            if (attribute.namespaceURI.isNullOrEmpty()) {
                println(attribute.localName)
                newElement.setAttribute(attribute.localName, content)
            } else {
                if (attribute.namespaceURI != "http://www.w3.org/2000/xmlns/") {

                    val prefix = getNamespacePrefix(attribute.namespaceURI)
                    newElement.setAttributeNS(attribute.namespaceURI, prefix + ":" + attribute.localName, content)
                }
            }
        }

        for (index in 0 until element.childNodes.length) {
            newElement.appendChild(handle(element.childNodes.item(index)))
        }

        if (isSection()) {
            addSectionNamespaces(newElement)
            section.removeLast()
        }
        path.removeLast()
        return newElement
    }

    private fun isSection(): Boolean {
        println(path.joinToString("."))
        return sections.contains(path.joinToString(".")) || path.size == 1
    }

    private fun addSectionNamespaces(element: Element) {

        val context = section.last()

        for ((namespace, prefix) in context.local.entries) {
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:$prefix", namespace)
        }
    }


    private fun handleComment(comment: Comment): Comment = document.createComment(comment.data)

    private fun handleText(text: Text): Text = document.createTextNode(text.data)

    private class NamespaceContext(
            private val global: MutableMap<String, String>,
            private val strategy: NamespaceStrategy,
            private val parent: NamespaceContext?
    ) {

        val local = HashMap<String, String>()

        fun getOrCreateNamespace(namespace: String): String {
            val prefix = getNamespace(namespace)
            if (prefix != null) {
                return prefix
            }
            return local.computeIfAbsent(namespace) {
                global.computeIfAbsent(it) {
                    ensureUnique(strategy.createNamespacePrefix(it))
                }
            }
        }

        fun getNamespace(namespace: String): String? {
            val prefix = parent?.getNamespace(namespace)
            if (prefix != null) {
                return prefix
            }
            return local[namespace]
        }


        private fun createGlobalPrefix(namespace: String): String {
            return global.computeIfAbsent(namespace) {
                ensureUnique(strategy.createNamespacePrefix(it))
            }
        }

        private fun ensureUnique(prefix: String): String {
            if (!global.containsValue(prefix)) {
                return prefix
            }
            var candidate = 0
            while (global.containsValue(prefix + candidate)) {
                candidate++
            }
            return prefix + candidate
        }


    }

    companion object {
        private val dbf = DocumentBuilderFactory.newInstance()!!
    }
}