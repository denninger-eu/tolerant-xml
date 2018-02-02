package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.WsdlInterfaceFactory
import com.eviware.soapui.impl.wsdl.*
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.*
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.XPathContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapResponseAssertion
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestRequestStepFactory
import com.eviware.soapui.model.iface.Operation
import com.eviware.soapui.model.testsuite.TestAssertion
import com.eviware.soapui.model.testsuite.TestStep
import eu.k5.tolerant.converter.soapui.listener.*
import java.io.File
import java.nio.file.Files

class CopyListener(private val wsdlSource: WsdlSource) : SuListener {
    private var project: WsdlProject? = null

    fun getBytes(): ByteArray {

        val file = File.createTempFile("soapui", "temp-project.xml")

        project!!.saveAs(file.absolutePath)

        return Files.readAllBytes(file.toPath())
    }


    override fun enterProject(env: Environment, wsdlProject: WsdlProject) {
        project = WsdlProject()
        project?.name = wsdlProject.name
        project?.description = wsdlProject.description

        for (property in wsdlProject.propertyList) {
            project!!.addProperty(property.name).value = property.value
        }

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

    private var lastRequestStep: WsdlTestRequestStep? = null

    override fun createAssertionListener(env: Environment, step: TestStep): SuAssertionListener? {
        if (lastRequestStep == null) {
            return null
        }
        if (lastRequestStep!!.name == step.name) {
            return CopyAssertions(lastRequestStep!!)
        } else {
            throw IllegalStateException("LastRequestStep does not match step for assertionListener")
        }

    }

    private fun copyBasicProperties(source: WsdlTestStep, target: WsdlTestStep) {
        target.config.disabled = source.isDisabled
        target.description = source.description
        for (property in source.propertyList) {


            //     target.setPropertyValue(property.name, property.value)
            //target.propertyList.add(property)
        }
    }

    override fun gotoStep(env: Environment, step: WsdlGotoTestStep) {

    }

    override fun transfer(env: Environment, step: PropertyTransfersTestStep) {
        val newStep = testCase.addTestStep("transfer", step.name) as PropertyTransfersTestStep

        copyBasicProperties(step, newStep)
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

        TODO("add dummy operation if necessary")
    }

    override fun request(env: Environment, step: WsdlTestRequestStep) {

        val operation = getOperationByName(step.operationName) as WsdlOperation

        val config = WsdlTestRequestStepFactory.createConfig(operation, step.name)

        val newStep = testCase.addTestStep(config) as WsdlTestRequestStep
        newStep.httpRequest.requestContent = step.httpRequest.requestContent

        copyBasicProperties(step, newStep)

        lastRequestStep = newStep
    }

    override fun delay(env: Environment, step: WsdlDelayTestStep) {
        val newStep = testCase.addTestStep("delay", step.name) as WsdlDelayTestStep
        newStep.delay = step.delay

        copyBasicProperties(step, newStep)
    }

}

class CopyAssertions(private val step: WsdlTestRequestStep) : SuAssertionListener {
    override fun soapFault(env: Environment, assertion: SoapFaultAssertion) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun soapResponse(env: Environment, assertion: SoapResponseAssertion) {
        val type = assertion.config.type

        val newAssertion = step.addAssertion(type) as SoapResponseAssertion

        newAssertion.config.name = assertion.config.name
        newAssertion.config.disabled = assertion.isDisabled
    }

    override fun xpathContains(env: Environment, assertion: XPathContainsAssertion) {

        val type = assertion.config.type

        val newAssertion = step.addAssertion(type) as XPathContainsAssertion

        newAssertion.path = assertion.path
        newAssertion.expectedContent = assertion.expectedContent
        newAssertion.config.disabled = assertion.isDisabled
    }

    override fun unsupported(env: Environment, assertion: TestAssertion) {
        println(assertion.javaClass.name)
    }

}