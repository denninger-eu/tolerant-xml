package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlTestSuite
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.PropertyTransfersTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import java.io.InputStream
import java.nio.file.Files


fun analyseSoapUiProject(inputStream: InputStream) {
    val project = WsdlProject(inputStream, null)

    val interfaces = project.interfaces

    for ((key, interfaze) in interfaces) {
        val wsdlInterface = interfaze as WsdlInterface
        //wsdlInterface.wsdlContext.
    }

}


class SoapUiConverter(inputStream: InputStream) {

    val sourceProject = WsdlProject(inputStream, null)
    val targetProject = WsdlProject()

    fun convert() {


        for ((key, interfaze) in sourceProject.interfaces) {

        }




        convertTestSuites()

    }

    fun get() {


    }

    fun convertTestSuites() {
        for ((name, suite) in sourceProject.testSuites) {

            if (suite is WsdlTestSuite) {
                val targetSuite = targetProject.addNewTestSuite(suite.name)


                convertTestCases(suite, targetSuite)


            }

        }
    }

    fun convertTestCases(sourceSuite: WsdlTestSuite, targetSuite: WsdlTestSuite) {

        for (sourceCase in sourceSuite.testCaseList) {
            if (sourceCase is WsdlTestCase) {
                val targetCase = targetSuite.addNewTestCase(sourceCase.name)
                targetCase.isDisabled = sourceCase.isDisabled


            }
        }
    }

    private fun convertTestCase(sourceCase: WsdlTestCase, targetCase: WsdlTestCase) {
        for (sourceStep in sourceCase.testStepList) {
            if (sourceStep is WsdlTestRequestStep) {


            } else if (sourceStep is PropertyTransfersTestStep) {

            }
        }
    }

    private fun convertTestStep(sourceStep: WsdlTestRequestStep) {

    }

}
