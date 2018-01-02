package eu.k5.tolerant.converter

enum class PreProcessing {
    REPAIR_NAMESPACES
}

data class TolerantConverterRequest(
        var content: String? = null,

        var preProcessing: List<PreProcessing>? = ArrayList()
)