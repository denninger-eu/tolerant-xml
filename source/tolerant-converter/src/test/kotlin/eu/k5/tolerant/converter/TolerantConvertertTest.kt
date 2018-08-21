package eu.k5.tolerant.converter

import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import eu.k5.tolerant.converter.TolerantConverterRequest
import eu.k5.tolerant.converter.config.ReaderConfig
import eu.k5.tolerant.converter.config.XsdContent
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

class TolerantConvertertTest {


    @Test
    fun withPath() {
        val reader = ReaderConfig()
        reader.xsd = "classpath:xs/import.xsd"
        val config = TolerantConverterConfiguration(key = "id", name = "test", reader = reader)

        val tolerantConverter = TolerantConverter(config)

        val request = TolerantConverterRequest()
        request.content = "<ListType>\n" +
                "    <list>abc</list>\n" +
                "    <list>edf</list>\n" +
                "</ListType>"
        val result = tolerantConverter.convert(request)
        println(result.content)

    }

    @Test
    fun withWsdl() {
        val files = ArrayList<String>()
        files.add("xs/stock.wsdl")

        val config = TolerantConverterConfiguration(key = "id", name = "test", reader = readerConfig(files))
        val tolerantConverter = TolerantConverter(config)
        val request = TolerantConverterRequest()
        request.content = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:stoc=\"http://example.com/stockquote.xsd\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <stoc:TradePriceRequest>\n" +
                "         <tickerSymbol>?</tickerSymbol>\n" +
                "      </stoc:TradePriceRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>"
        val result = tolerantConverter.convert(request)
        println(result.content)
    }

    @Test
    fun withContent() {
        val files = ArrayList<String>()
        files.add("xs/import.xsd")
        files.add("xs/simple-type-list.xsd")
        files.add("xs/simple-type-enumeration.xsd")
        files.add("xs/simple-type-nillable.xsd")
        files.add("xs/simple-type-date.xsd")
        files.add("xs/simple-type-numeric.xsd")
        files.add("xs/simple-type-misc.xsd")
        files.add("xs/simple-type-restrictionbases.xsd")
        files.add("xs/complex-type-idref.xsd")
        files.add("xs/complex-type-simplecontent.xsd")
        files.add("xs/inheritance/complex-type-inheritance-base.xsd")
        files.add("xs/inheritance/complex-type-inheritance.xsd")
        files.add("xs/inheritance/complex-type-inheritance-sub.xsd")
        files.add("xs/complex-type.xsd")
        files.add("xs/strict/strict-minimal.xsd")



        val config = TolerantConverterConfiguration(key = "id", name = "test", reader = readerConfig(files))
        val tolerantConverter = TolerantConverter(config)

        val request = TolerantConverterRequest()
        request.content = "<ListType>\n" +
                "    <list>abc</list>\n" +
                "    <list>edf</list>\n" +
                "</ListType>"
        val result = tolerantConverter.convert(request)
        println(result.content)
    }

    private fun readerConfig(files: List<String>): ReaderConfig {
        val xsdContents = ArrayList<XsdContent>()
        for (file in files) {
            val xsdContent = XsdContent()
            xsdContent.content = load(file)
            xsdContent.name = file
            xsdContents.add(xsdContent)
        }

        val readerConfig = ReaderConfig()
        readerConfig.xsd = files[0]
        readerConfig.xsdContent = xsdContents

        return readerConfig
    }

    private fun load(file: String): String {
        val inputStream = TolerantConvertertTest::class.java.classLoader.getResourceAsStream(file)

        return inputStream.use {
            BufferedReader(InputStreamReader(it)).lines().collect(Collectors.joining("\n"))
        }
    }
}