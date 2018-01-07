package eu.k5.tolerantreader

import org.slf4j.LoggerFactory

enum class Type {
    MISSING_SETTER,

    UNSUPPORTED_BASE_TYPE,

    MISSING_GETTER,

    UNKNOWN_SCHEMA,

    MISSING_TYPE_ADAPTER
}

class InitContext {
    fun addFinding(type: Type, s: String) {
        LOGGER.warn(type.toString() + ": " + s)

    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(InitContext::class.java)
    }


}