package eu.k5.tolerant.webclient.converter

import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.TolerantConverterRequest
import eu.k5.tolerant.converter.TolerantConverterResult
import eu.k5.tolerant.converter.config.Configurations
import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors
import javax.xml.bind.JAXBContext


class Converter {

    private val configs: MutableMap<String, TolerantConverterConfiguration> = HashMap()

    private val converter = ConcurrentHashMap<String, TolerantConverter>()

    init {
        val base = Paths.get("config")

        LOGGER.info("Loading converter targets  from: {}", base.toAbsolutePath())

        val load = Configurations.Companion.load(base.resolve("xs.config.xml"))

        for (config in load.getAll()) {
            configs.put(config.key, config)
        }
    }

    fun listAvailable(): List<TolerantConverterConfiguration> {
        return ArrayList(configs.values)
    }

    fun convert(target: String?, request: TolerantConverterRequest): TolerantConverterResult {
        val conv = getConverter(target!!)
        return conv.convert(request)
    }

    private fun getConverter(target: String): TolerantConverter {
        return converter.computeIfAbsent(target) {
            TolerantConverter(configs.get(target)!!)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Converter::class.java)
    }
}