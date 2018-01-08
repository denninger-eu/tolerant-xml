package eu.k5.tolerant.config

import eu.k5.tolerant.converter.config.Configurations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class ConfigurationsTest {


    @Test
    fun test() {
        val config = Configurations.load(Paths.get("src", "test", "resources", "config", "example.xml"))

        val converterConfiguration = config.queryConverterConfiguration("conv")

        assertEquals("conv", converterConfiguration.key)
    }
}