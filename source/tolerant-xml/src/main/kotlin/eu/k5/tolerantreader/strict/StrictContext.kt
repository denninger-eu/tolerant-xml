package eu.k5.tolerantreader.strict

class StrictContext(private val schema: StrictSchema) {

    private val additionalPrefixes: MutableMap<String, String> = HashMap()

    fun resolveType(type: Class<*>)
            = schema.getType(type)


    fun getAllNamespaces() = schema.namespaces


    fun getNamespacePrefix(namespace: String): String? {
        val prefix = schema.namespaces.get(namespace)
        if (prefix != null) {
            return prefix
        }
        return additionalPrefixes[namespace]
    }

    fun createPrefix(namespace: String): String {
        val rawPrefix = "axs"
        var index = 0;
        var prefix = rawPrefix + index
        while (additionalPrefixes.containsValue(prefix)) {
            prefix = rawPrefix + index++
        }

        additionalPrefixes.put(namespace, prefix)
        return prefix
    }
}