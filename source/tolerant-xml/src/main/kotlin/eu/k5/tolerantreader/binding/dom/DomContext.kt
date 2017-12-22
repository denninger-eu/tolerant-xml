package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.tolerant.XSI_NAMESPACE
import org.w3c.dom.Document

class DomContext(val document: Document) {

    private val prefixes: MutableMap<String, String> = HashMap()

    fun getPrefix(namespace: String): String
            = prefixes.computeIfAbsent(namespace) { createPrefix(it) }

    fun getUsedNamespaces(): Map<String, String> = prefixes

    private fun createPrefix(namespace: String): String {
        var rawPrefix = rawPrefix(namespace)
        var index = 0
        var prefix = rawPrefix
        while (prefixes.containsValue(prefix)) {
            prefix = rawPrefix + index++
        }
        return prefix
    }

    private fun rawPrefix(namespace: String): String {
        if (namespace.equals(XSI_NAMESPACE)) {
            return "xsi"
        }

        return "ns"

    }
}