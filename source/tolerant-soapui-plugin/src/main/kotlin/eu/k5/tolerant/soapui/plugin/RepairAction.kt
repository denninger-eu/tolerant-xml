package eu.k5.tolerant.soapui.plugin

import com.eviware.soapui.impl.wsdl.WsdlRequest
import com.eviware.soapui.plugins.ActionConfiguration
import com.eviware.soapui.plugins.ToolbarPosition
import com.eviware.soapui.support.action.support.AbstractSoapUIAction
import eu.k5.tolerant.converter.TolerantConverterRequest
import eu.k5.tolerant.converter.TolerantConverterResult
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import javax.swing.SwingUtilities

@ActionConfiguration(actionGroup = "WsdlRequestActions", //
        toolbarPosition = ToolbarPosition.NONE, //
        toolbarIcon = "/favicon.png", //
        description = "Repair Dialog")//
class RepairAction : AbstractSoapUIAction<WsdlRequest>("Repair Dialog", "Repairs Request") {

  /*  init {
        SwingUtilities.invokeLater() {
            val editor = RepairEditor("Example", "<test></test>") { TolerantConverterResult(it) }
            editor.isVisible = true
            editor.dispose()
        }

    }*/

    override fun perform(request: WsdlRequest, o: Any?) {
        try {
            val tolerantConverter = createTolerantConverter(request.operation.`interface`)

            SwingUtilities.invokeLater() {
                val editor = RepairEditor(request.name, request.requestContent) {
                    val converterRequest = TolerantConverterRequest()
                    converterRequest.content = it
                    tolerantConverter.convert(converterRequest)
                }
                editor.ok = { request.requestContent = it }
                editor.isVisible = true


            }

        } catch (e: Throwable) {
            e.printStackTrace()
        }

        System.out.println("repair");
    }
}
