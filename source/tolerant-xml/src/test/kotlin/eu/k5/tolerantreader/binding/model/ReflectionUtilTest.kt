package eu.k5.tolerantreader.binding.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ReflectionUtilTest {

    private var utils = ReflectionUtils()

    @Test
    @DisplayName("Sanitize Single Letter Name")
    fun sanitizeName() {
        Assertions.assertEquals("A", utils.sanitizeAsClassName("a"))
    }

    @Test
    @DisplayName("Sanitize Name with all lowercase, first character is uppercase")
    fun sanitizeName_alllowercase() {
        Assertions.assertEquals("Root", utils.sanitizeAsClassName("root"))
    }


    @Test
    @DisplayName("Sanitize Name with underscore, underscore is worddelimiter")
    fun sanitizeName_underscore() {
        Assertions.assertEquals("RootRoot", utils.sanitizeAsClassName("root_root"))
    }

}