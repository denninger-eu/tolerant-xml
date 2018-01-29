package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.*
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import eu.k5.tolerantreader.xsDatetime
import java.io.InputStream

class SoapUiParser() {


    fun parse(inputStream: InputStream, visitor: SoapUiVisitor) {
        val project = WsdlProject(inputStream, null)

        val env = SoapUiVisitor.Environment()
        visitor.enterProject(env, project)


        for (interfaze in project.interfaceList) {

            if (interfaze !is WsdlInterface) {
                visitor.unsupportedInterface(env, interfaze)
            }

            visitor.enterInterface(env, interfaze)


            for (operation in interfaze.operationList) {
                if (operation !is WsdlOperation) {
                    visitor.unsupportedOperation(env, operation)
                }
                visitor.enterOperation(env, operation)
                for (request in operation.requestList) {

                    visitor.request(env, request as WsdlRequest)
                }
            }
            visitor.exitInterface(env, interfaze)
        }

    }

    private fun parseSuites(env: SoapUiVisitor.Environment, visitor: SoapUiVisitor, project: WsdlProject) {
        for (suite in project.testSuiteList) {
            if (suite !is WsdlTestSuite) {
                visitor.unsupportedTestSuite(env, suite)
                continue
            }
            visitor.enterTestSuite(env, suite)

            for (testCase in suite.testCaseList) {
                if (testCase !is WsdlTestCase) {
                    visitor.unsupportedTestCase(env, testCase)
                    continue
                }
                visitor.enterTestCase(env, testCase)

                for(step in testCase.testStepList){

                }
                visitor.exitTestCase(env, testCase)
            }

            visitor.exitTestSuite(env, suite)
        }
    }


    private fun parseTestSteps(){

    }
}