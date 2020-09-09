package eu.k5.tolerant.converter

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EnsurePrefixesPreprocessorTest {

    @Test
    fun missingPrefix() {
        val fixed = EnsurePrefixesPreprocessor().ensurePrefixes("""<abc yy:test=""></abc>""")

        assertSimilar("""<abc xmlns:yy="http://dread/inserted" yy:test=""/>""", fixed)
    }


    private fun assertSimilar(expected: String, actual: String) {
        if (sanitized(expected) != sanitized(actual)) {
            println(sanitized(actual))
            Assertions.assertEquals(expected, actual)
        }
    }

    private fun sanitized(string: String): String {
        val sanitized = string.lines().map { it.trim() }.filter { !it.isBlank() }.filter { it != "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" }.joinToString("\n")

        return sanitized
    }
}