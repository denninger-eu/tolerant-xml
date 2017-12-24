package eu.k5.tolerantreader.binding.dom

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConfigurableNamespaceStrategyTest {

    private var unitUnderTest: NamespaceStrategy? = null


    @Test
    fun explicit() {
        val strategy = ConfigurableNamespaceStrategy(createConfiguration())
        assertEquals("ab", strategy.createNamespacePrefix("http://abc"))
    }

    @Test
    fun pattern() {
        val strategy = ConfigurableNamespaceStrategy(createConfiguration())
        assertEquals("abcd", strategy.createNamespacePrefix("http://abcd"))
    }

    private fun createConfiguration(): Configuration {
        val configuration = Configuration()
        configuration.explicit.put("http://abc", "ab")
        configuration.pattern.add(Conditional("http://.*", "http://(?<prefix>.*)"))
        return configuration
    }
}