package eu.k5.tolerantxml.client.repair

import eu.k5.soapui.fx.NewTabEvent
import eu.k5.tolerant.converter.config.ReaderConfig
import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import javafx.scene.Parent
import tornadofx.*
import javax.annotation.PostConstruct

fun main(args: Array<String>) {
    tornadofx.launch<RepairStarter>(*args)
}

class RepairStarter : App(RepairStarterView::class) {
    val view: RepairStarterView by inject()

    init {
        RepairModule().init()
    }
}


class RepairStarterView() : View() {
    override val root = borderpane { }

    init {
        println("before new tab subscribe")
        subscribe<NewTabEvent> {
            println("New Tab received")
            root.center = it.tab.content
        }
    }

    override fun onDock() {
        super.onDock()
        val event = CreateTolerantConverter("name", "file:/C:/data/stock.wsdl")
        event.xsds["file:/C:/data/stock.wsdl"] = "<wsdl:definitions targetNamespace=\"http://k5.eu/dread/countries\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:sch=\"http://k5.eu/dread/countries\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:tns=\"http://k5.eu/dread/countries\">\n" +
                "   <wsdl:types>\n" +
                "      <xs:schema elementFormDefault=\"qualified\" targetNamespace=\"http://k5.eu/dread/countries\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "         <xs:complexType name=\"country\">\n" +
                "            <xs:sequence>\n" +
                "               <xs:element name=\"name\" type=\"xs:string\"/>\n" +
                "               <xs:element name=\"population\" type=\"xs:int\"/>\n" +
                "               <xs:element name=\"capital\" type=\"xs:string\"/>\n" +
                "            </xs:sequence>\n" +
                "         </xs:complexType>\n" +
                "         <xs:element name=\"getCountryRequest\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"id\" type=\"xs:string\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"getCountryResponse\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"country\" type=\"tns:country\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"findCountryRequest\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"name\" type=\"xs:string\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"findCountryResponse\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"country\" type=\"tns:country\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"getCountriesRequest\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence/>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"getCountriesResponse\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"country\" type=\"tns:country\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"createCountryRequest\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"country\" type=\"tns:country\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"createCountryResponse\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"country\" type=\"tns:country\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"updateCountryRequest\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"country\" type=\"tns:country\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"updateCountryResponse\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"country\" type=\"tns:country\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"deleteCountryRequest\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"name\" type=\"xs:string\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "         <xs:element name=\"deleteCountryResponse\">\n" +
                "            <xs:complexType>\n" +
                "               <xs:sequence>\n" +
                "                  <xs:element name=\"country\" type=\"tns:country\"/>\n" +
                "               </xs:sequence>\n" +
                "            </xs:complexType>\n" +
                "         </xs:element>\n" +
                "      </xs:schema>\n" +
                "   </wsdl:types>\n" +
                "   <wsdl:message name=\"findCountryRequest\">\n" +
                "      <wsdl:part element=\"tns:findCountryRequest\" name=\"findCountryRequest\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"createCountryRequest\">\n" +
                "      <wsdl:part element=\"tns:createCountryRequest\" name=\"createCountryRequest\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"deleteCountryRequest\">\n" +
                "      <wsdl:part element=\"tns:deleteCountryRequest\" name=\"deleteCountryRequest\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"getCountriesResponse\">\n" +
                "      <wsdl:part element=\"tns:getCountriesResponse\" name=\"getCountriesResponse\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"getCountryRequest\">\n" +
                "      <wsdl:part element=\"tns:getCountryRequest\" name=\"getCountryRequest\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"deleteCountryResponse\">\n" +
                "      <wsdl:part element=\"tns:deleteCountryResponse\" name=\"deleteCountryResponse\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"findCountryResponse\">\n" +
                "      <wsdl:part element=\"tns:findCountryResponse\" name=\"findCountryResponse\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"createCountryResponse\">\n" +
                "      <wsdl:part element=\"tns:createCountryResponse\" name=\"createCountryResponse\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"getCountriesRequest\">\n" +
                "      <wsdl:part element=\"tns:getCountriesRequest\" name=\"getCountriesRequest\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"updateCountryResponse\">\n" +
                "      <wsdl:part element=\"tns:updateCountryResponse\" name=\"updateCountryResponse\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"getCountryResponse\">\n" +
                "      <wsdl:part element=\"tns:getCountryResponse\" name=\"getCountryResponse\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:message name=\"updateCountryRequest\">\n" +
                "      <wsdl:part element=\"tns:updateCountryRequest\" name=\"updateCountryRequest\"/>\n" +
                "   </wsdl:message>\n" +
                "   <wsdl:portType name=\"CountriesPort\">\n" +
                "      <wsdl:operation name=\"findCountry\">\n" +
                "         <wsdl:input message=\"tns:findCountryRequest\" name=\"findCountryRequest\"/>\n" +
                "         <wsdl:output message=\"tns:findCountryResponse\" name=\"findCountryResponse\"/>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"createCountry\">\n" +
                "         <wsdl:input message=\"tns:createCountryRequest\" name=\"createCountryRequest\"/>\n" +
                "         <wsdl:output message=\"tns:createCountryResponse\" name=\"createCountryResponse\"/>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"deleteCountry\">\n" +
                "         <wsdl:input message=\"tns:deleteCountryRequest\" name=\"deleteCountryRequest\"/>\n" +
                "         <wsdl:output message=\"tns:deleteCountryResponse\" name=\"deleteCountryResponse\"/>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"getCountries\">\n" +
                "         <wsdl:input message=\"tns:getCountriesRequest\" name=\"getCountriesRequest\"/>\n" +
                "         <wsdl:output message=\"tns:getCountriesResponse\" name=\"getCountriesResponse\"/>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"getCountry\">\n" +
                "         <wsdl:input message=\"tns:getCountryRequest\" name=\"getCountryRequest\"/>\n" +
                "         <wsdl:output message=\"tns:getCountryResponse\" name=\"getCountryResponse\"/>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"updateCountry\">\n" +
                "         <wsdl:input message=\"tns:updateCountryRequest\" name=\"updateCountryRequest\"/>\n" +
                "         <wsdl:output message=\"tns:updateCountryResponse\" name=\"updateCountryResponse\"/>\n" +
                "      </wsdl:operation>\n" +
                "   </wsdl:portType>\n" +
                "   <wsdl:binding name=\"CountriesPortSoap11\" type=\"tns:CountriesPort\">\n" +
                "      <soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n" +
                "      <wsdl:operation name=\"findCountry\">\n" +
                "         <soap:operation soapAction=\"\"/>\n" +
                "         <wsdl:input name=\"findCountryRequest\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:input>\n" +
                "         <wsdl:output name=\"findCountryResponse\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:output>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"createCountry\">\n" +
                "         <soap:operation soapAction=\"\"/>\n" +
                "         <wsdl:input name=\"createCountryRequest\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:input>\n" +
                "         <wsdl:output name=\"createCountryResponse\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:output>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"deleteCountry\">\n" +
                "         <soap:operation soapAction=\"\"/>\n" +
                "         <wsdl:input name=\"deleteCountryRequest\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:input>\n" +
                "         <wsdl:output name=\"deleteCountryResponse\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:output>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"getCountries\">\n" +
                "         <soap:operation soapAction=\"\"/>\n" +
                "         <wsdl:input name=\"getCountriesRequest\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:input>\n" +
                "         <wsdl:output name=\"getCountriesResponse\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:output>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"getCountry\">\n" +
                "         <soap:operation soapAction=\"\"/>\n" +
                "         <wsdl:input name=\"getCountryRequest\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:input>\n" +
                "         <wsdl:output name=\"getCountryResponse\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:output>\n" +
                "      </wsdl:operation>\n" +
                "      <wsdl:operation name=\"updateCountry\">\n" +
                "         <soap:operation soapAction=\"\"/>\n" +
                "         <wsdl:input name=\"updateCountryRequest\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:input>\n" +
                "         <wsdl:output name=\"updateCountryResponse\">\n" +
                "            <soap:body use=\"literal\"/>\n" +
                "         </wsdl:output>\n" +
                "      </wsdl:operation>\n" +
                "   </wsdl:binding>\n" +
                "   <wsdl:service name=\"CountriesPortService\">\n" +
                "      <wsdl:port binding=\"tns:CountriesPortSoap11\" name=\"CountriesPortSoap11\">\n" +
                "         <soap:address location=\"http://localhost:8080/ws\"/>\n" +
                "      </wsdl:port>\n" +
                "   </wsdl:service>\n" +
                "</wsdl:definitions>"


        fire(event)
        println("Event fired")
    }

}

