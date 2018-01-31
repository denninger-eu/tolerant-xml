package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.WsdlInterfaceFactory
import com.eviware.soapui.impl.wsdl.*
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.PropertyTransfersTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDelayTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.teststeps.registry.PropertyTransfersStepFactory
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestRequestStepFactory
import com.eviware.soapui.model.iface.Operation
import eu.k5.tolerant.converter.soapui.listener.*

class CopyListener(private val wsdlSource: WsdlSource) : SuListener {
    private var project: WsdlProject? = null


    override fun enterProject(env: Environment, wsdlProject: WsdlProject) {
        project = WsdlProject()
        project?.name = wsdlProject.name
        project?.description = wsdlProject.description
    }

    override fun exitProject() {
    }

    override fun createWsdlInterfaceListener(): SuInterfaceListener {
        return CopyInterface(wsdlSource, project!!)
    }

    override fun createWsdlTestSuiteListener(): SuWsdlTestSuiteListener {
        return CopyTestSuite(project!!)
    }

}

class CopyInterface(
        private val wsdlSource: WsdlSource,
        private val project: WsdlProject
) : SuInterfaceListener {

    private var currentInterface: WsdlInterface? = null

    private var currentOperation: WsdlOperation? = null

    override fun enterInterface(env: Environment, wsdlInterface: WsdlInterface) {

        val wsdlLocation = wsdlSource.getWsdlLocation(wsdlInterface.name)

        val importWsdl = WsdlInterfaceFactory.importWsdl(project, wsdlLocation, false)
        currentInterface = importWsdl[0]

        project.interfaceList.add(currentInterface)
    }

    override fun exitInterface(env: Environment, wsdlInterface: WsdlInterface) {
    }


    override fun enterOperation(env: Environment, operation: WsdlOperation) {
        if (currentInterface != null) {
            currentOperation = currentInterface!!.operations[operation.name] as WsdlOperation
        }
    }

    override fun exitOperation(env: Environment, operation: WsdlOperation) {
    }

    override fun request(env: Environment, wsdlRequest: WsdlRequest) {

        val newRequest = currentOperation!!.addNewRequest(wsdlRequest.name)

        newRequest.requestContent = wsdlRequest.requestContent
    }

}

class CopyTestSuite(private val project: WsdlProject) : SuWsdlTestSuiteListener {

    private var currentTestSuite: WsdlTestSuite? = null
    private var currentTestCase: WsdlTestCase? = null

    override fun enterTestSuite(env: Environment, suite: WsdlTestSuite) {
        currentTestSuite = project.addNewTestSuite(suite.name)
    }

    override fun exitTestSuite(env: Environment, suite: WsdlTestSuite) {
    }


    override fun enterTestCase(env: Environment, testCase: WsdlTestCase) {
        currentTestCase = currentTestSuite?.addNewTestCase(testCase.name)

    }

    override fun exitTestCase(env: Environment, testCase: WsdlTestCase) {
    }

    override fun createTestStepListener(): SuTestStepListener {
        return CopyTestStep(currentTestCase!!)
    }

}

class CopyTestStep(private val testCase: WsdlTestCase) : SuTestStepListener {

    override fun transfer(env: Environment, step: PropertyTransfersTestStep) {
        PropertyTransfersStepFactory().
    }


    private fun getOperationByName(operationName: String): Operation {
        testCase.testSuite.project.interfaceList
                .filterIsInstance<WsdlInterface>()
                .forEach {
                    for (operation in it.allOperations) {
                        if (operation.name == operationName) {
                            return operation
                        }
                    }
                }

        // Create detect catchall interface/operation
        TODO("add error handling")
    }

    override fun request(env: Environment, step: WsdlTestRequestStep) {


        val operation = getOperationByName(step.operationName) as WsdlOperation

        val config = WsdlTestRequestStepFactory.createConfig(operation, step.name)

        val newStep = testCase.addTestStep(config) as WsdlTestRequestStep
        newStep.httpRequest.requestContent = step.httpRequest.requestContent
    }

    override fun delay(env: Environment, step: WsdlDelayTestStep) {
        val newStep = testCase.addTestStep("delay", step.name) as WsdlDelayTestStep
        newStep.delay = step.delay
    }

}