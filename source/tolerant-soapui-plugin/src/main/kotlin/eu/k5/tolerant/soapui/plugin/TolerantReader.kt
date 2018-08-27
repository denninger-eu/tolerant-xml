package eu.k5.tolerant.soapui.plugin

import com.eviware.soapui.impl.wsdl.WsdlInterface
import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.config.ReaderConfig
import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import eu.k5.tolerant.converter.config.WriterConfig
import eu.k5.tolerant.converter.config.XsdContent

fun createTolerantConverter(iface: WsdlInterface): TolerantConverter {

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

    val writerConfig = WriterConfig()


    val config = TolerantConverterConfiguration(key = "id", name = "test", reader = readerConfig)
    return TolerantConverter(config)
}