package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.WsdlInterfaceFactory
import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.impl.wsdl.WsdlOperation
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlTestSuite
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlDefinitionLoader
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.PropertyTransfersTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import eu.k5.tolerant.converter.WsdlSource
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


class SoapUiConverter(inputStream: InputStream, private val wsdlSource: WsdlSource) {

    val sourceProject = WsdlProject(inputStream, null)

    var description = SoapUiDescription()

    var interfaceDescription: SoapUiInterface? = null

    fun convert(): ByteArray {
        val targetProject = WsdlProject()

        targetProject.name = sourceProject.name
        targetProject.description = sourceProject.description

        convertInterfaces(targetProject)
        convertTestSuites(targetProject)

        val tempFile = Files.createTempFile("soapui-convert", "project.xml")
        targetProject.saveAs(tempFile.toAbsolutePath().toString())

        return Files.readAllBytes(tempFile)

    }

    private fun convertInterfaces(targetProject: WsdlProject) {

        for (interfaze in sourceProject.interfaceList) {
            val wsdl = wsdlSource.getWsdlLocation(interfaze.name)
            if (interfaze is WsdlInterface) {
                println(interfaze.interfaceType)

//                val targetInterface = targetProject.addNewInterface(interfaze.name, interfaze.interfaceType) as WsdlInterface

                val targetInterface = WsdlInterfaceFactory.importWsdl(targetProject, wsdlSource.getWsdlLocation(interfaze.name), false)[0]

                interfaceDescription = SoapUiInterface(interfaze.name, supported = true)
                description.interfaces!!.add(interfaceDescription!!)
                convertInterface(interfaze, targetInterface)
                interfaceDescription = null

            } else {
                description.interfaces!!.add(SoapUiInterface(interfaze.name, supported = false))
            }
        }
    }

    private fun convertInterface(sourceInterface: WsdlInterface, targetInterface: WsdlInterface) {
        for (operation in sourceInterface.operationList) {
            val targetOperation = targetInterface.operations[operation.name] as WsdlOperation?
            if (targetOperation == null) {
                interfaceDescription!!.operation.add(SoapUiOperation(operation.name, supported = false))
            } else {
                for (sourceRequest in operation.requestList) {
                    val targetRequest = targetOperation.addNewRequest(sourceRequest.name)
                }
            }
        }
    }

    fun convertTestSuites(targetProject: WsdlProject) {
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

    fun getTargetBytes() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
