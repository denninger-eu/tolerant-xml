package eu.k5.tolerantreader.binding.dom

import java.util.regex.Pattern


class ConfigurableNamespaceStrategy(configuration: Configuration) : NamespaceStrategy {

    private val explicit: Map<String, String> = configuration.explicit

    private val fallback: String = configuration.fallback

    private val conditionals: List<ConditionalPrefix>

    init {
        val cond = ArrayList<ConditionalPrefix>()
        for (pattern in configuration.pattern) {
            val use = Pattern.compile(pattern.use)
            val extract = Pattern.compile(pattern.extract)
            cond.add(ConditionalPrefix(use, extract, configuration.fallback))
        }
        conditionals = cond
    }


    override fun createNamespacePrefix(namespace: String): String {
        if (explicit.containsKey(namespace)) {
            return explicit[namespace] ?: fallback
        }
        return this.conditionals
                .firstOrNull { it.use.matcher(namespace).matches() }
                ?.create(namespace)
                ?: fallback
    }

    data class ConditionalPrefix(
            val use: Pattern,
            private val extract: Pattern,
            private var fallback: String
    ) {

        fun create(namespace: String): String {
            val matcher = extract.matcher(namespace)
            if (matcher.matches()) {
                return matcher.group("prefix")
            }
            return fallback
        }
    }
}

class Configuration {
    val explicit: MutableMap<String, String> = HashMap()

    val pattern: MutableList<Conditional> = ArrayList()

    val fallback: String = "ns"

}

class Conditional(val use: String, val extract: String)

