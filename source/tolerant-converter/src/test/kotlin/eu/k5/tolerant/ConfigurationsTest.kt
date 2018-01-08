package eu.k5.tolerant

import eu.k5.tolerant.converter.Configurations
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class ConfigurationsTest {


    @Test
    fun test() {
        val config = Configurations.load(Paths.get("src", "test", "resources", "config", "example.xml"))

    }
}