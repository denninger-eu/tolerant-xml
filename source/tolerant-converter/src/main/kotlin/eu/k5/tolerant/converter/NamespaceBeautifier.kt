package eu.k5.tolerant.converter

import eu.k5.tolerant.converter.config.Explicit
import eu.k5.tolerantreader.binding.dom.ConfigurableNamespaceStrategy
import eu.k5.tolerantreader.binding.dom.NamespaceStrategy
import org.w3c.dom.*
import java.lang.IllegalStateException
import javax.xml.parsers.DocumentBuilderFactory

class NamespaceBeautifier(
        private val namespaceStrategy: NamespaceStrategy
) {
    private val document = dbf.newDocumentBuilder().newDocument()

    private val prefixes = HashMap<String, String>()

    private fun getNamespacePrefix(namespace: String): String {
        return prefixes.computeIfAbsent(namespace) {
            namespaceStrategy.createNamespacePrefix(namespace)
        }
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
                    if (newElement.lookupNamespaceURI(valueNamespaceUri) == null) {
                        newElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + getNamespacePrefix(valueNamespaceUri), valueNamespaceUri)

                    }
                    getNamespacePrefix(valueNamespaceUri) + ":" + attribute.textContent.substring(attribute.textContent.indexOf(':') + 1)

                } else {
                    attribute.textContent
                }
            }

            if (attribute.namespaceURI.isNullOrEmpty()) {
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
        return newElement
    }

    private fun handleComment(comment: Comment): Comment = document.createComment(comment.data)

    private fun handleText(text: Text): Text = document.createTextNode(text.data)

    companion object {
        private val dbf = DocumentBuilderFactory.newInstance()!!

    }
}