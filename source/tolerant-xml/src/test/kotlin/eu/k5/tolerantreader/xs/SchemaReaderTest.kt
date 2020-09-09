package eu.k5.tolerantreader.xs

import org.junit.jupiter.api.Test

class SchemaReaderTest {

    @Test
    fun parseFromMap() {
        val map = createMap()

        val parse = Schema.parse("main.wsdl", createMap())

    }


    companion object {

        fun createMap(): HashMap<String, String> {
            val map = HashMap<String, String>()
            map["main.wsdl"] = """<?xml version="1.0" ?>
    <wsdl:definitions targetNamespace="urn:listing2"
    xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <wsdl:types>
    <xsd:schema>
    <xsd:include schemaLocation="./include.xsd"/>
     </xsd:schema>
    </wsdl:types>
   
    </wsdl:definitions>"""

            map["include.xsd"] = """<schema xmlns="http://www.w3.org/2001/XMLSchema"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        targetNamespace="http://k5.eu/tr/model">

    <xs:complexType name="personinfo">
        <xs:attribute name="attrib" type="xs:string"/>
    </xs:complexType>


</schema>"""

            return map
        }
    }
}