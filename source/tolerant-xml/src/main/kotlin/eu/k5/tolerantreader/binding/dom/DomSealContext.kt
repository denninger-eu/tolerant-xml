package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.reader.TolerantReaderConfiguration
import org.w3c.dom.Document

class DomSealContext(val document: Document, val namespaceStrategy: NamespaceStrategy) {

    constructor(document: Document, readerConfiguration: TolerantReaderConfiguration) : this(document, readerConfiguration.queryConfigOrDefault(NamespaceStrategy::class.java) {
        DefaultNamespaceStrategy
    })


    private val prefixes: MutableMap<String, String> = HashMap()


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


    fun subContext(): DomSealContext {
        return DomSealContext(document, namespaceStrategy)
    }

}