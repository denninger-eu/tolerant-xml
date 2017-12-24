package eu.k5.tolerantreader.binding.dom

interface NamespaceStrategy {

    fun createNamespacePrefix(namespace: String): String
}