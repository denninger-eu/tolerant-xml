package eu.k5.tolerant.soapui.plugin

import com.eviware.soapui.impl.wsdl.WsdlRequest
import com.eviware.soapui.support.action.support.AbstractSoapUIAction
import com.eviware.soapui.plugins.ToolbarPosition
import com.eviware.soapui.plugins.ActionConfiguration
import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.TolerantConverterRequest
import eu.k5.tolerant.converter.TolerantConverterResult
import eu.k5.tolerant.converter.config.ReaderConfig
import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import eu.k5.tolerant.converter.config.XsdContent
import javax.swing.SwingUtilities


@ActionConfiguration(actionGroup = "WsdlRequestActions", //
        toolbarPosition = ToolbarPosition.NONE, //
        toolbarIcon = "/favicon.png", //
        description = "Quick Repair Tool")//
class QuickRepairAction : AbstractSoapUIAction<WsdlRequest>("Quick Repair", "Repairs Request") {



    override fun perform(request: WsdlRequest, o: Any?) {
        try {
            val tolerantConverter = createTolerantConverter(request.operation.`interface`)

            val converterRequest = TolerantConverterRequest()
            converterRequest.content = request.requestContent
            val result = tolerantConverter.convert(converterRequest)

            request.requestContent = result.content
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        System.out.println("repair");

    }

}