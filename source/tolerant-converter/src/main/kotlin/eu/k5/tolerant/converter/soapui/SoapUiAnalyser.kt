package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlTestSuite
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import java.io.InputStream

class SoapUiAnalyser(inputStream: InputStream) {

    val description = SoapUiDescription()


    init {
        val sourceProject = WsdlProject(inputStream, null)

        try {

            description.name = sourceProject.name

            for (interfaze in sourceProject.interfaceList) {

                val intf = SoapUiInterface(name = interfaze.name)


                for (operation in interfaze.operationList) {
                    for (request in operation.requestList) {

                        val soapUiRequest = SoapUiRequest(request.name, request.operation.name)
                        intf.requests!!.add(soapUiRequest)

                        println(request.name)
                        println(request.operation.name)
                    }
                }
                description.interfaces!!.add(intf)
            }
            description.suites!!.addAll(analyseTestSuites(sourceProject))
        } finally {
            // Make sure project is closed
        }
    }

    private fun analyseTestSuites(sourceProject: WsdlProject): List<SoapUiTestSuite> {
        val suites = ArrayList<SoapUiTestSuite>()
        for (suite in sourceProject.testSuiteList) {
            if (suite is WsdlTestSuite) {
                suites.add(analyseTestSuite(suite))
            } else {
                suites.add(SoapUiTestSuite(name = suite.name, supported = false))
            }
        }
        return suites
    }

    private fun analyseTestSuite(sourceSuite: WsdlTestSuite): SoapUiTestSuite {
        val suite = SoapUiTestSuite(sourceSuite.name, supported = true)

        for (testCase in sourceSuite.testCaseList) {

            val caze = if (testCase is WsdlTestCase) {
                analyseTestCase(testCase)
            } else {
                SoapUiTestCase(testCase.name, supported = false)
            }

            suite.cases!!.add(caze)
        }
        return suite
    }

    private fun analyseTestCase(testCase: WsdlTestCase): SoapUiTestCase {
        val caze = SoapUiTestCase(name = testCase.name, supported = true)

        for (step in testCase.testStepList) {

        }
        return caze
    }

}