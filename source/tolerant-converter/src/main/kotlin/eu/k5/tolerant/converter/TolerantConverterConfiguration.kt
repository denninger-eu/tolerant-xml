package eu.k5.tolerant.converter


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


    var convert

    fun queryConverterConfiguration(key: String) {



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


@XmlRootElement(name = "ConverterConfig")
@XmlAccessorType(XmlAccessType.NONE)
class TolerantConverterConfiguration {

    @XmlAttribute(name = "key")
    var key: String? = null

    @XmlElement(name = "name")
    var name: String? = null

    @XmlElement(name = "reader")
    var reader: ReaderConfig? = null


    @XmlElement(name = "namespaces")
    var namespaces: NamespacePrefixConfig? = null
}


enum class NumberParsers {
    US, DE, DETECT;
}

@XmlAccessorType(XmlAccessType.NONE)
class ReaderConfig {

    @XmlElement(name = "xsd")
    var xsd: String? = null

    @XmlElement(name = "numbers")
    var numberParser: NumberParsers? = null


    @XmlElement(name = "lengthRestriction")
    var enforceLengthRestriction: Boolean? = true

}

@XmlAccessorType(XmlAccessType.NONE)
class NamespacePrefixConfig {

    @XmlAttribute(name = "fallback")
    var fallback: String = "xs"

    @XmlElement(name = "explicit")
    var explicit: List<Explicit> = ArrayList()

    @XmlElement(name = "pattern")
    var pattern: List<Pattern> = ArrayList()
}

@XmlAccessorType(XmlAccessType.NONE)
class Explicit {

    @XmlAttribute(name = "namespace")
    var namespace: String? = null

    @XmlAttribute(name = "prefix")
    var prefix: String? = null
}

@XmlAccessorType(XmlAccessType.NONE)
class Pattern {
    @XmlAttribute(name = "use")
    var use: String? = null

    @XmlAttribute(name = "extract")
    var extract: String? = null
}