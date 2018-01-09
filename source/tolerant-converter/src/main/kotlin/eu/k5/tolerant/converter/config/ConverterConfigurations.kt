package eu.k5.tolerant.converter.config


import eu.k5.tolerantreader.binding.dom.NamespaceStrategy
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path
import javax.xml.bind.JAXBContext
import javax.xml.bind.annotation.*

@XmlRootElement(name = "ConverterConfig")
@XmlAccessorType(XmlAccessType.NONE)

class Configurations {

    @XmlElement(name = "reader")
    var readers: List<ReaderConfig>? = ArrayList()

    @XmlElement(name = "writer")
    var writers: List<WriterConfig>? = ArrayList()

    @XmlElement(name = "converter")
    var converter: List<ConverterConfig>? = ArrayList()

    fun queryConverterConfiguration(key: String): TolerantConverterConfiguration {
        val converterConfig = getConverterConfig(key)
        return initConfig(converterConfig)
    }

    private fun initConfig(converterConfig: ConverterConfig): TolerantConverterConfiguration {
        val readerConfig = getReaderConfig(converterConfig.readerRef!!)
        val writerConfig = getWriterConfig(converterConfig.writerRef!!)

        val configs = HashMap<Class<*>, Any>()
        configs.put(NamespaceStrategy::class.java, writerConfig.createNamespaceStrategy())
        return TolerantConverterConfiguration(converterConfig, readerConfig, writerConfig, configs)
    }


    fun getAll(): List<TolerantConverterConfiguration> {
        return converter.orEmpty().map {
            initConfig(it)
        }.toCollection(ArrayList())
    }


    private fun getReaderConfig(key: String): ReaderConfig {
        readers.orEmpty()
                .filter { key == it.key }
                .forEach { return it }
        throw IllegalArgumentException("Converter with key $key not found")

    }

    private fun getWriterConfig(key: String): WriterConfig {
        writers.orEmpty()
                .filter { key == it.key }
                .forEach { return it }
        throw IllegalArgumentException("Converter with key $key not found")

    }

    private fun getConverterConfig(key: String): ConverterConfig {
        converter.orEmpty()
                .filter { key == it.key }
                .forEach { return it }
        throw IllegalArgumentException("Converter with key $key not found")
    }

    companion object {

        private var jaxbContext = JAXBContext.newInstance(Configurations::class.java)

        fun load(path: Path): Configurations {
            return load(FileInputStream(path.toFile()))
        }

        fun load(inputStream: InputStream): Configurations {
            val obj = jaxbContext.createUnmarshaller().unmarshal(inputStream)
            if (obj is Configurations) {
                return obj
            } else {
                throw IllegalArgumentException("Configuration file contains wrong type: " + obj.javaClass)
            }
        }
    }
}


class TolerantConverterConfiguration(
        val key: String,
        val name: String,
        val xsd: String,
        val configs: Map<Class<*>, Any> = HashMap()

) {

    constructor(converterConfig: ConverterConfig, readerConfig: ReaderConfig, writerConfig: WriterConfig, configs: Map<Class<*>, Any>)
            : this(key = converterConfig.key!!, name = converterConfig.name!!, xsd = readerConfig.xsd!!, configs = HashMap(configs))
}



