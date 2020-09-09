package eu.k5.tolerant.soapui.plugin

import com.eviware.soapui.impl.wsdl.WsdlInterface
import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.config.*
import java.util.*

fun createTolerantConverter(iface: WsdlInterface): TolerantConverter {
    val writerConfig = createWriterConfiguration()
    val readerConfig = createReaderConfig(iface)

    val config = TolerantConverterConfiguration(key = "id", name = "test", reader = readerConfig, writer = writerConfig)
    return TolerantConverter(config)
}

private fun createReaderConfig(iface: WsdlInterface, key: String = "standard"): ReaderConfig {
    val xsdContents = ArrayList<XsdContent>()

    for (part in iface.getDefinitionContext().getDefinitionParts()) {
        System.out.println(part.getType() + " " + part.getUrl() + " " + part.getContent());
        val xsdContent = XsdContent()
        xsdContent.content = part.content
        xsdContent.name = part.url
        xsdContents.add(xsdContent)
    }

    val readerConfig = ReaderConfig()
    readerConfig.xsd = iface.definitionContext.definitionParts[0].url
    readerConfig.xsdContent = xsdContents
    readerConfig.key = key
    return readerConfig
}

fun createWriterConfiguration(key: String = "standard"): WriterConfig {
    val writerConfig = WriterConfig()
    writerConfig.key = key
    return writerConfig
}

fun createConverterConfiguration(iface: WsdlInterface): Configurations {

    val config = Configurations()
    config.transformers = ArrayList()
    config.readers = Arrays.asList(createReaderConfig(iface))
    config.writers = Arrays.asList(createWriterConfiguration())

    val converterConfig = ConverterConfig()
    converterConfig.readerRef = "standard"
    converterConfig.writerRef = "standard"
    converterConfig.key = "standard"
    converterConfig.name = "standard"

    config.converter = Arrays.asList(converterConfig)
    return config
}