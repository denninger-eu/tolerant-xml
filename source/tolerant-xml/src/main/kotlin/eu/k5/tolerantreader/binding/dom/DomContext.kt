package eu.k5.tolerantreader.binding.dom

import org.w3c.dom.Document

class DomContext(val document: Document) {

    private val prefixes: MutableMap<String, String> = HashMap()

    private val namespaceStrategy: NamespaceStrategy = DefaultNamespaceStrategy()

    fun getPrefix(namespace: String): String
            = prefixes.computeIfAbsent(namespace) { createUniquePrefix(it) }

    fun getUsedNamespaces(): Map<String, String> = prefixes


    private fun createUniquePrefix(namespace: String): String {
        var rawPrefix = namespaceStrategy.createNamespacePrefix(namespace)
        var index = 0
        var prefix = rawPrefix
        while (prefixes.containsValue(prefix)) {
            prefix = rawPrefix + index++
        }
        return prefix
    }


}