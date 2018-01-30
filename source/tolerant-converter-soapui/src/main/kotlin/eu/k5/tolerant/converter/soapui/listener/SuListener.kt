package eu.k5.tolerant.converter.soapui.listener

import com.eviware.soapui.impl.wsdl.WsdlProject

interface SuListener {

    fun enterProject(env: Environment, project: WsdlProject)

    fun exitProject()

    fun createWsdlInterfaceListener(): SuInterfaceListener

    fun createWsdlTestSuiteListener(): SuWsdlTestSuiteListener


}