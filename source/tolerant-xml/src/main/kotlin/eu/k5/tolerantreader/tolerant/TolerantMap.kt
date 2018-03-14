package eu.k5.tolerantreader.tolerant

import com.google.common.collect.ImmutableMap
import java.lang.IllegalStateException
import java.util.*
import javax.xml.namespace.QName
import kotlin.collections.ArrayList

/*
enum class TolerantMapStrategy {
    STRICT {
        override fun createNamespace(all: List<String>): String {
            return ""
        }
    },
    LONGEST_PREFIX, LONGEST_SUFFIX, LEVENSTEENDISTANCE

    abstract fun createNamespace(all: List<String>): String
}*/
private fun padding(length: Int): String = ":".repeat(length)


class TolerantMap<T>(
        private val internal: ImmutableMap<String, T>,
        private val prefixLength: ImmutableMap<String, Int>,
        private val caseSensitive: Boolean
) {

    fun getByLocalName(localPart: String): T? {
        if (prefixLength.containsKey(localPart)) {
            throw IllegalStateException("Value is not unique for that localPart")
        }
        return internal[localPart]
    }

    fun get(namespaceURI: String, localPart: String): T? {
        val prefix = prefixLength[localPart]
        if (prefix != null) {
            if (namespaceURI.length >= prefix) {
                val key: String =
                        if (!caseSensitive) {
                            namespaceURI.substring(0, prefix).toLowerCase() + localPart.toLowerCase()
                        } else {
                            namespaceURI.substring(0, prefix) + localPart
                        }
                return internal[key]
            } else {
                val key: String =
                        if (!caseSensitive) {
                            namespaceURI.toLowerCase() + padding(prefix - namespaceURI.length) + localPart.toLowerCase()
                        } else {
                            namespaceURI + padding(prefix - namespaceURI.length) + localPart
                        }

                return internal[key]
            }
        }

        return if (internal.containsKey(localPart)) {
            internal[localPart]
        } else {
            internal[localPart.toLowerCase()]
        }
    }

    fun getAllByLocalName(localName: String): List<T> {
        if (!prefixLength.containsKey(localName)) {
            return Arrays.asList(internal[localName]!!)
        }

        val length = prefixLength[localName]!! + localName.length

        val all = ArrayList<T>()
        for (entry in internal.entries) {
            if (entry.key.length == length && entry.key.endsWith(localName)) {
                all.add(entry.value)
            }
        }
        return all

    }

    fun get(name: QName): T? = get(name.namespaceURI, name.localPart)

    fun isEmpty(): Boolean = internal.isEmpty()

    fun values(): Collection<T>
            = internal.values

    companion object {

        fun <T> of(elements: Map<QName, T>): TolerantMap<T> {
            val builder = TolerantMapBuilder<T>()
            for ((key, value) in elements) {
                builder.append(key, value)
            }
            return builder.build()
        }

        fun <T> of(elements: Collection<T>, getQName: (T) -> QName): TolerantMap<T> {
            val builder = TolerantMapBuilder<T>()
            for (element in elements) {
                builder.append(getQName(element), element)
            }
            return builder.build()

        }
    }
}

class TolerantMapBuilder<T>(val caseSensitive: Boolean = true) {

    private var internal: MutableMap<QName, T> = HashMap()

    private var namespacesByLocalPart: MutableMap<String, NamespaceCollection> = HashMap()


    fun append(name: QName, element: T) {
        internal.put(name, element)
        namespacesByLocalPart.computeIfAbsent(name.localPart) {
            NamespaceCollection(caseSensitive)
        }.namespaces.add(name.namespaceURI)
    }

    fun build(): TolerantMap<T> {
        val elementPrefixLength = ImmutableMap.builder<String, Int>()
        val tolerantBuilder = ImmutableMap.builder<String, T>()

        internal.forEach { (name, element) ->
            val collection = namespacesByLocalPart[name.localPart]!!
            if (collection.prefixRequired()) {
                if (collection.seal()) {
                    elementPrefixLength.put(name.localPart, collection.prefix)

                }

                val prefix = collection.prefixMap[name.namespaceURI]
                tolerantBuilder.put(prefix + name.localPart, element)
            } else {
                tolerantBuilder.put(name.localPart, element)
                if (!caseSensitive && name.localPart != name.localPart.toLowerCase()) {
                    tolerantBuilder.put(name.localPart.toLowerCase(), element)
                }
            }
        }

        return TolerantMap(tolerantBuilder.build(), elementPrefixLength.build(), caseSensitive)
    }


}

class NamespaceCollection(val caseSensitive: Boolean) {
    val namespaces: MutableSet<String> = HashSet()

    val prefixMap: MutableMap<String, String> = HashMap()
    var prefix: Int? = null
    var sealed: Boolean = false
    private fun calculatePrefixLength(): Int {
        val maxLength: List<String> = createMaxLength(namespaces)

        var min = 0
        var max = maxLength[0].length

        while (max - min > 1) {
            val length = (min + max) / 2
            val size = withLength(length, maxLength).size
            if (size != maxLength.size) {
                min = length
            } else {
                max = length
            }
        }
        return max
    }

    private fun withLength(length: Int, all: List<String>): Set<String>
            = all.mapTo(HashSet()) { it.substring(0, length) }


    private fun createMaxLength(all: Set<String>): List<String> {
        if (all.isEmpty()) {
            return ArrayList()
        }
        val longestLength: Int = all.map { it.length }.max()!!

        val allMax = ArrayList<String>()
        for (s in all) {
            if (s.length >= longestLength) {
                allMax.add(s)
            } else {
                allMax.add(s + padding(longestLength - s.length))
            }
        }
        return allMax
    }

    fun prefixRequired(): Boolean = namespaces.size > 1

    /**
     * Returns true iff was not already sealed
     */
    fun seal(): Boolean {
        if (sealed) {
            return false
        }
        if (namespaces.size < 2) {
            throw IllegalStateException("Can not seal namespace collection with only " + namespaces.size + " elements")
        }
        val prefix = calculatePrefixLength()
        this.prefix = prefix

        for (namespace in namespaces) {
            if (namespace.length < prefix) {
                prefixMap.put(namespace, namespace + padding(prefix - namespace.length))
            } else {
                prefixMap.put(namespace, namespace.substring(0, prefix))
            }
        }
        sealed = true
        return true
    }


}
