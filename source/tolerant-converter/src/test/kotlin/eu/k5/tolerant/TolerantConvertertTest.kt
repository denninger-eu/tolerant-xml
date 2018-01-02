package eu.k5.tolerant

import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.TolerantConverterConfiguration
import eu.k5.tolerant.converter.TolerantConverterRequest
import org.junit.jupiter.api.Test

class TolerantConvertertTest {


    @Test
    fun test() {
        val config = TolerantConverterConfiguration()
        config.name = "test"
        config.xsd = "classpath:xs/import.xsd"
        val tolerantConverter = TolerantConverter(config)

        val request = TolerantConverterRequest()
        request.content = "<ListType>\n" +
                "    <list>abc</list>\n" +
                "    <list>edf</list>\n" +
                "</ListType>"
        val result = tolerantConverter.convert(request)
        println(result.content)

    }
}