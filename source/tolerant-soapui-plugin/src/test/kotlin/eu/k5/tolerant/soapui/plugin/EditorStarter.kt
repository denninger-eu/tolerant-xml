package eu.k5.tolerant.soapui.plugin

import eu.k5.tolerant.converter.config.*
import java.io.StringWriter
import java.io.Writer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.xml.bind.JAXB

fun main(args: Array<String>) {
    val request = createRequest()
    val writer = StringWriter()
    JAXB.marshal(request, writer)
    val bytes = writer.toString().toByteArray(StandardCharsets.UTF_8)
    Files.write(Paths.get("request.xml"), bytes)

    val repair = Repair(request)
    repair.start()
}


private fun createRequest(): RepairRequest {
    val request = RepairRequest()
    request.converter = createConverterConfiguration()
    request.converterKey = "standard"
    request.name = "test"
    request.request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ns:Envelope xmlns:ns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "   <ns:Header/>\n" +
            "   <ns:Body>\n" +
            "      <ns:TradePriceRequest xmlns:ns=\"http://example.com/stockquote.xsd\">\n" +
            "         <ns:tickerSymbol>1212</ns:tickerSymbol>\n" +
            "      </ns:TradePriceRequest>\n" +
            "   </ns:Body>\n" +
            "</ns:Envelope>"
    return request
}

private fun createConverterConfiguration(): Configurations {
    val configurations = Configurations()
    val writerConfig = WriterConfig()
    writerConfig.key = "standard"
    configurations.writers = Arrays.asList(writerConfig)
    val readerConfig = ReaderConfig()
    readerConfig.key = "standard"
    val xsdContent = XsdContent()
    xsdContent.content = "<definitions name=\"StockQuote\"\n" +
            "             targetNamespace=\"http://example.com/stockquote.wsdl\"\n" +
            "             xmlns:tns=\"http://example.com/stockquote.wsdl\"\n" +
            "             xmlns:xsd1=\"http://example.com/stockquote.xsd\"\n" +
            "             xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"\n" +
            "             xmlns=\"http://schemas.xmlsoap.org/wsdl/\">\n" +
            "\n" +
            "    <types>\n" +
            "        <schema targetNamespace=\"http://example.com/stockquote.xsd\"\n" +
            "                xmlns=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "            <element name=\"TradePriceRequest\">\n" +
            "                <complexType>\n" +
            "                    <sequence>\n" +
            "                        <element name=\"tickerSymbol\" type=\"string\"/>\n" +
            "                    </sequence>\n" +
            "                </complexType>\n" +
            "            </element>\n" +
            "            <element name=\"TradePrice\">\n" +
            "                <complexType>\n" +
            "                    <sequence>\n" +
            "                        <element name=\"price\" type=\"float\"/>\n" +
            "                    </sequence>\n" +
            "                </complexType>\n" +
            "            </element>\n" +
            "        </schema>\n" +
            "    </types>\n" +
            "\n" +
            "    <message name=\"GetLastTradePriceInput\">\n" +
            "        <part name=\"body\" element=\"xsd1:TradePriceRequest\"/>\n" +
            "    </message>\n" +
            "\n" +
            "    <message name=\"GetLastTradePriceOutput\">\n" +
            "        <part name=\"body\" element=\"xsd1:TradePrice\"/>\n" +
            "    </message>\n" +
            "\n" +
            "    <portType name=\"StockQuotePortType\">\n" +
            "        <operation name=\"GetLastTradePrice\">\n" +
            "            <input message=\"tns:GetLastTradePriceInput\"/>\n" +
            "            <output message=\"tns:GetLastTradePriceOutput\"/>\n" +
            "        </operation>\n" +
            "    </portType>\n" +
            "\n" +
            "    <binding name=\"StockQuoteSoapBinding\" type=\"tns:StockQuotePortType\">\n" +
            "        <soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n" +
            "        <operation name=\"GetLastTradePrice\">\n" +
            "            <soap:operation soapAction=\"http://example.com/GetLastTradePrice\"/>\n" +
            "            <input>\n" +
            "                <soap:body use=\"literal\"/>\n" +
            "            </input>\n" +
            "            <output>\n" +
            "                <soap:body use=\"literal\"/>\n" +
            "            </output>\n" +
            "        </operation>\n" +
            "    </binding>\n" +
            "\n" +
            "    <service name=\"StockQuoteService\">\n" +
            "        <documentation>My first service</documentation>\n" +
            "        <port name=\"StockQuotePort\" binding=\"tns:StockQuoteSoapBinding\">\n" +
            "            <soap:address location=\"http://example.com/stockquote\"/>\n" +
            "        </port>\n" +
            "    </service>\n" +
            "\n" +
            "</definitions>"
    xsdContent.name = "stock.wsdl"
    readerConfig.xsdContent = Arrays.asList(xsdContent)
    readerConfig.xsd = "stock.wsdl"
    configurations.readers = Arrays.asList(readerConfig)


    val converter = ConverterConfig()
    converter.key = "standard"
    converter.name = "Standard"
    converter.writerRef = "standard"
    converter.readerRef = "standard"

    configurations.converter = Arrays.asList(converter)
    return configurations
}