package eu.k5.tolerant.config

import eu.k5.tolerant.converter.config.Configurations
import eu.k5.tolerant.converter.config.NamespacePrefixConfig
import eu.k5.tolerantreader.binding.dom.NamespaceStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import javax.naming.Name
import javax.xml.stream.events.Namespace

class ConfigurationsTest {


    @Test
    fun test() {
        val config = Configurations.load(Paths.get("src", "test", "resources", "config", "example.xml"))

        val converterConfiguration = config.queryConverterConfiguration("conv")

        assertEquals("conv", converterConfiguration.key)
        val ns = converterConfiguration.configs[NamespaceStrategy::class.java] as NamespaceStrategy?

        assertEquals("soap", ns!!.createNamespacePrefix("http://schemas.xmlsoap.org/wsdl/soap/"))

        assertEquals("abc", ns!!.createNamespacePrefix("http://abc/V"))
    }
}