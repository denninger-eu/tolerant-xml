package eu.k5.tolerant.converter.soapui

interface WsdlSource {


    fun getWsdlLocation(name: String): String

}