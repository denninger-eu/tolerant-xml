package eu.k5.tolerantreader.xs

import com.sun.xml.internal.ws.api.server.SDDocument
import eu.k5.tolerantreader.WSDL_NAMESPACE
import eu.k5.tolerantreader.XSD_NAMESPACE
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "definitions", namespace = WSDL_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
class WsdlDefinitions {

    @XmlElement(name = "types", namespace = WSDL_NAMESPACE)
    var types: WsdlTypes? = null

}

@XmlAccessorType(XmlAccessType.NONE)
class WsdlTypes {

    @XmlElement(name = "schema", namespace = XSD_NAMESPACE)
    var schema: XsSchema? = null

}