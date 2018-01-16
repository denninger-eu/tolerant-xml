package eu.k5.tolerant.converter.soapui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths

class SoapUiAnalyserTest {

    @Test
    fun test() {
        val bytes = Files.readAllBytes(Paths.get("src", "test", "resources", "ex", "ex-soapui-project.xml"))
        val analyser = SoapUiAnalyser(ByteArrayInputStream(bytes))


        println(analyser.description.name)
        assertEquals("ex", analyser.description.name)

        println(analyser.description)
    }
}