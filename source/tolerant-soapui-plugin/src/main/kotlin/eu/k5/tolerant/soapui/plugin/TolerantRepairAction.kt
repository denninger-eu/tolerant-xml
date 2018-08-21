package eu.k5.tolerant.soapui.plugin

import com.eviware.soapui.impl.wsdl.WsdlRequest
import com.eviware.soapui.support.action.support.AbstractSoapUIAction
import com.eviware.soapui.plugins.ToolbarPosition
import com.eviware.soapui.plugins.ActionConfiguration
import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.TolerantConverterRequest
import eu.k5.tolerant.converter.config.ReaderConfig
import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import eu.k5.tolerant.converter.config.XsdContent


@ActionConfiguration(actionGroup = "WsdlRequestActions", //
        toolbarPosition = ToolbarPosition.NONE, //
        toolbarIcon = "/favicon.png", //
        description = "Jwt Token Configuration Tool")//
class TolerantRepairAction : AbstractSoapUIAction<WsdlRequest>("Repair", "Repairs Request") {


    override fun perform(request: WsdlRequest, o: Any?) {

        val interface1 = request.operation.`interface`

        try {

            val xsdContents = ArrayList<XsdContent>()

            for (part in interface1.getDefinitionContext().getDefinitionParts()) {
                System.out.println(part.getType() + " " + part.getUrl() + " " + part.getContent());

                val xsdContent = XsdContent()
                xsdContent.content = part.content
                xsdContent.name = part.url
                xsdContents.add(xsdContent)

            }

            val readerConfig = ReaderConfig()
            readerConfig.xsd = interface1.definitionContext.definitionParts[0].url
            readerConfig.xsdContent = xsdContents

            val config = TolerantConverterConfiguration(key = "id", name = "test", reader = readerConfig)
            val tolerantConverter = TolerantConverter(config)


            val converterRequest = TolerantConverterRequest()
            converterRequest.content = request.requestContent
            val result = tolerantConverter.convert(converterRequest)
            println(result.content)


        } catch (e: Throwable) {
            e.printStackTrace()
        }

        System.out.println("repair");

    }

}