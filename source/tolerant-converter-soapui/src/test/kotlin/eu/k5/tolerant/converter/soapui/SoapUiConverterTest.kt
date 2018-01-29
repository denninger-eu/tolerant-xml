package eu.k5.tolerant.converter.soapui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths

class SoapUiConverterTest {

    @Test
    fun test() {
        val bytes = Files.readAllBytes(Paths.get("src", "test", "resources", "ex", "ex-soapui-project.xml"))

        val filter = ConvertUiFilter()

        val converter = SoapUiConverter(ByteArrayInputStream(bytes), StaticWsdlSource("src/test/resources/ex/ex.txt"))
        val converted = converter.convert()

        val actualDescription = SoapUiAnalyser(converted).description
        var expectedDescription = SoapUiAnalyser(ByteArrayInputStream(bytes)).description

        assertEquals(expectedDescription, actualDescription)

    }


    class StaticWsdlSource(private val static: String) : WsdlSource {
        override fun getWsdlLocation(name: String): String = static

    }
}