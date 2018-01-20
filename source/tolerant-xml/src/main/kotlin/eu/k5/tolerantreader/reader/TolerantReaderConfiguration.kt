package eu.k5.tolerantreader.reader

import org.slf4j.LoggerFactory
import java.util.*

class TolerantReaderConfiguration(init: Map<Class<*>, Any>) {
    private val configs: Map<Class<*>, Any> = Collections.unmodifiableMap(HashMap(init))

    fun <T> queryConfigOrDefault(type: Class<T>, createDefault: () -> T): T {
        val obj = configs[type]
        if (type.isInstance(obj)) {
            return type.cast(obj)
        }
        LOGGER.debug("Accessing not existing config: " + type)
        return createDefault()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TolerantReader::class.java)
    }

}