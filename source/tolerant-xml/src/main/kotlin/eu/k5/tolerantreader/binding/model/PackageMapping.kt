package eu.k5.tolerantreader.binding.model

import com.google.common.collect.ImmutableMap

class PackageMapping(val mappings: ImmutableMap<String, String>) {

    fun getPackage(namespace: String): String {
        val pckage = mappings.get(namespace)
                ?: throw IllegalArgumentException("No package mapping for namespace:" + namespace)
        return pckage
    }
}

class PackageMappingBuilder {
    private val mappings = ImmutableMap.builder<String, String>()
    fun add(namespace: String, packge: String): PackageMappingBuilder {
        mappings.put(namespace, packge)
        return this
    }

    fun build(): PackageMapping {
        return PackageMapping(mappings.build())
    }


}