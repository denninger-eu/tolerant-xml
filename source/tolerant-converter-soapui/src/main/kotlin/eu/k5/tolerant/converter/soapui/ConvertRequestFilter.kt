package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.WsdlProject
import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.soapui.listener.Environment
import eu.k5.tolerant.converter.soapui.listener.SuInterfaceListener
import eu.k5.tolerant.converter.soapui.listener.SuListener
import eu.k5.tolerant.converter.soapui.listener.SuWsdlTestSuiteListener

class ConvertRequestFilter : SuListener {
    override fun exitProject() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createWsdlInterfaceListener(): SuInterfaceListener {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createWsdlTestSuiteListener(): SuWsdlTestSuiteListener {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enterProject(env: Environment, project: WsdlProject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}