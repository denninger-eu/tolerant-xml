package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.tolerant.XSI_NAMESPACE

class DefaultNamespaceStrategy : NamespaceStrategy {

    override fun createNamespacePrefix(namespace: String): String {
        if (namespace == XSI_NAMESPACE) {
            return "xsi"
        }
        return "ns"
    }

}