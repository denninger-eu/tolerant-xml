package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlTestSuite
import java.io.InputStream

class SoapUiAnalyser(inputStream: InputStream) {

    val sourceProject = WsdlProject(inputStream, null)

    val description = SoapUiDescription()

    fun analyse() {
        for (interfaze in sourceProject.interfaceList) {
            for(operation in  interfaze.operationList){
                for(request in operation.requestList){

                }
            }
        }
        analyseTestSuites()
    }

    private fun analyseTestSuites(): List<SoapUiTestSuite> {
        val suites = ArrayList<SoapUiTestSuite>()
        for (suite in sourceProject.testSuiteList) {
            if (suite is WsdlTestSuite) {
                analyseTestSuite(suite)
            } else {
                suites.add(SoapUiTestSuite(name = suite.name, supported = false))
            }
        }
        return suites
    }

    private fun analyseTestSuite(suite: WsdlTestSuite) {

    }

}