package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.*
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.PropertyTransfersTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDelayTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGotoTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.model.iface.Interface
import com.eviware.soapui.model.iface.Operation
import com.eviware.soapui.model.testsuite.TestCase
import com.eviware.soapui.model.testsuite.TestSuite
import eu.k5.tolerant.converter.soapui.listener.*

class AnalyseListener : SuListener {
    val description = SoapUiDescription()


    override fun enterProject(env: Environment, project: WsdlProject) {
        description.name = project.name
        description.description = project.description
    }

    override fun exitProject() {
    }

    override fun createWsdlInterfaceListener(): SuInterfaceListener = InterfaceAnalyser(description)

    override fun createWsdlTestSuiteListener(): SuWsdlTestSuiteListener = TestSuiteAnalyser(description)


}

class TestSuiteAnalyser(private val description: SoapUiDescription) : SuWsdlTestSuiteListener {
    override fun createTestStepListener(): SuTestStepListener {
        return StepAnalyser(currentCase!!)
    }

    private var suite: SoapUiTestSuite? = null

    private var currentCase: SoapUiTestCase? = null

    override fun unsupportedTestSuite(env: Environment, suite: TestSuite) {
        description.suites?.add(SoapUiTestSuite(suite.name, supported = false))
    }

    override fun enterTestSuite(env: Environment, testSuite: WsdlTestSuite) {
        suite = SoapUiTestSuite(testSuite.name, supported = true)
    }

    override fun exitTestSuite(env: Environment, testSuite: WsdlTestSuite) {
        if (suite != null) {
            description.suites?.add(suite!!)
        }
        suite = null
    }

    override fun unsupportedTestCase(env: Environment, testCase: TestCase) {
        suite?.cases?.add(SoapUiTestCase(testCase.name, supported = false))
    }

    override fun enterTestCase(env: Environment, testCase: WsdlTestCase) {
        currentCase = SoapUiTestCase(name = testCase.name, supported = true)
    }

    override fun exitTestCase(env: Environment, testCase: WsdlTestCase) {
        if (currentCase != null) {
            suite?.cases?.add(currentCase!!)
        }
    }

}

class InterfaceAnalyser(private val description: SoapUiDescription) : SuInterfaceListener {

    private var currentInterfaze: SoapUiInterface? = null
    private var currentOperation: SoapUiOperation? = null

    override fun unsupportedInterface(env: Environment, interfaze: Interface) {
    }

    override fun enterInterface(env: Environment, wsdlInterface: WsdlInterface) {

        currentInterfaze = SoapUiInterface(name = wsdlInterface.name, supported = true)

    }

    override fun exitInterface(env: Environment, wsdlInterface: WsdlInterface) {
        if (currentInterfaze != null) {
            description.interfaces?.add(currentInterfaze!!)
            currentInterfaze = null
        }
    }

    override fun unsupportedOperation(env: Environment, operation: Operation) {
    }

    override fun enterOperation(env: Environment, operation: WsdlOperation) {
        currentOperation = SoapUiOperation(operation.name, true)

    }

    override fun exitOperation(env: Environment, operation: WsdlOperation) {
        if (currentOperation != null) {
            currentInterfaze?.operation?.add(currentOperation!!)
        }
    }

    override fun request(env: Environment, wsdlRequest: WsdlRequest) {

        if (currentOperation != null) {
            val request = SoapUiRequest(wsdlRequest.name, currentOperation!!.name)

            currentInterfaze!!.requests.add(request)
        }

    }


}

class StepAnalyser(private val testCase: SoapUiTestCase) : SuTestStepListener {
    override fun gotoStep(env: Environment, step: WsdlGotoTestStep) {
        testCase?.steps?.add(SoapUiTestStep(step.name, "goto"))
    }

    override fun transfer(env: Environment, step: PropertyTransfersTestStep) {
        testCase?.steps?.add(SoapUiTestStep(step.name, "transfer"))
    }

    override fun delay(env: Environment, step: WsdlDelayTestStep) {
        testCase?.steps?.add(SoapUiTestStep(step.name, "delay"))
    }

    override fun request(env: Environment, step: WsdlTestRequestStep) {
        testCase?.steps?.add(SoapUiTestStep(step.name, "request"))
    }

}