package eu.k5.tolerant.converter.soapui

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths

class SoapUiAnalyserTest {

    @Test
    fun test() {
        val bytes = Files.readAllBytes(Paths.get("src", "test", "resources", "ex", "ex-soapui-project.xml"))


        val listener = AnalyseListener()

        val soapUiParser = SoapUiParser()

        soapUiParser.parse(ByteArrayInputStream(bytes), listener)


        println(listener.description.name)

        val jsonString = ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listener.description)

        println(jsonString)

    }
}