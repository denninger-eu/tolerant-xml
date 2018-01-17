package eu.k5.tolerantreader

import eu.k5.tolerantreader.tolerant.NumberFormat
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

    fun getAdapter(){

    }

    fun getNumberFormat(): NumberFormat {
        return NumberFormat.EN_US
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(InitContext::class.java)!!
    }


}