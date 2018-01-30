package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.impl.wsdl.WsdlOperation
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlRequest
import eu.k5.tolerant.converter.soapui.listener.Environment
import eu.k5.tolerant.converter.soapui.listener.SuInterfaceListener
import eu.k5.tolerant.converter.soapui.listener.SuListener
import eu.k5.tolerant.converter.soapui.listener.SuWsdlTestSuiteListener

class CopyListener : SuListener {
    private var project: WsdlProject? = null

    override fun enterProject(env: Environment, wsdlProject: WsdlProject) {
        project = WsdlProject()
        project?.name = wsdlProject.name
        project?.description = wsdlProject.description
    }

    override fun exitProject() {
    }

    override fun createWsdlInterfaceListener(): SuInterfaceListener {
        return CopyInterface(project!!)
    }

    override fun createWsdlTestSuiteListener(): SuWsdlTestSuiteListener {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class CopyInterface(private val project: WsdlProject) : SuInterfaceListener {

    override fun enterInterface(env: Environment, wsdlInterface: WsdlInterface) {


    }

    override fun exitInterface(env: Environment, wsdlInterface: WsdlInterface) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun enterOperation(env: Environment, operation1: WsdlOperation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun exitOperation(env: Environment, operation: WsdlOperation) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun request(env: Environment, wsdlRequest: WsdlRequest) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}