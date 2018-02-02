package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.*
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.PropertyTransfersTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDelayTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGotoTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.XPathContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapResponseAssertion
import eu.k5.tolerant.converter.soapui.listener.*
import java.io.InputStream

class SoapUiParser() {


    fun parse(inputStream: InputStream, mainListener: SuListener) {
        val project = WsdlProject(inputStream, null)

        val env = Environment()
        mainListener.enterProject(env, project)

        val interfaceListener = mainListener.createWsdlInterfaceListener()
        if (interfaceListener != null) {
            parseInterface(project, env, interfaceListener)
        }

        val testSuiteListener = mainListener.createWsdlTestSuiteListener()
        if (testSuiteListener != null) {
            parseSuites(project, env, testSuiteListener)
        }

    }

    private fun parseInterface(project: WsdlProject, env: Environment, listener: SuInterfaceListener) {

        for (interfaze in project.interfaceList) {

            if (interfaze !is WsdlInterface) {
                listener.unsupportedInterface(env, interfaze)
                continue
            }
            listener.enterInterface(env, interfaze)

            for (operation in interfaze.operationList) {
                if (operation !is WsdlOperation) {
                    listener.unsupportedOperation(env, operation)
                    continue
                }
                listener.enterOperation(env, operation)
                for (request in operation.requestList) {

                    listener.request(env, request as WsdlRequest)
                }
                listener.exitOperation(env, operation)
            }

            listener.exitInterface(env, interfaze)
        }

    }

    private fun parseSuites(project: WsdlProject, env: Environment, listener: SuWsdlTestSuiteListener) {
        for (suite in project.testSuiteList) {
            if (suite !is WsdlTestSuite) {
                listener.unsupportedTestSuite(env, suite)
                continue
            }
            listener.enterTestSuite(env, suite)

            for (testCase in suite.testCaseList) {
                if (testCase !is WsdlTestCase) {
                    listener.unsupportedTestCase(env, testCase)
                    continue
                }
                listener.enterTestCase(env, testCase)

                val testStepListener = listener.createTestStepListener()

                parseTestSteps(testCase, env, testStepListener)

                listener.exitTestCase(env, testCase)
            }

            listener.exitTestSuite(env, suite)
        }
    }


    private fun parseTestSteps(testCase: WsdlTestCase, env: Environment, listener: SuTestStepListener) {

        val steps = testCase.testStepList

        for (step in steps) {

            if (step is WsdlTestRequestStep) {
                listener.request(env, step)
                parseAssertions(listener, env, step)

            } else if (step is WsdlDelayTestStep) {
                listener.delay(env, step)
            } else if (step is PropertyTransfersTestStep) {
                listener.transfer(env, step)
            } else if (step is WsdlGotoTestStep) {
                listener.gotoStep(env, step)
            } else {
                listener.unsupported(env, step)
            }

        }

    }

    private fun parseAssertions(listener: SuTestStepListener, env: Environment, step: WsdlTestRequestStep) {
        val assertionListener = listener.createAssertionListener(env, step)
        if (assertionListener != null) {

            for (assertion in step.assertionList) {

                if (assertion is XPathContainsAssertion) {
                    assertionListener.xpathContains(env, assertion)
                } else if (assertion is SoapFaultAssertion) {

                    assertionListener.soapFault(env, assertion)
                } else if (assertion is SoapResponseAssertion) {
                    assertionListener.soapResponse(env, assertion)
                } else {

                    assertionListener.unsupported(env, assertion)
                }

            }

        }

    }

}