package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.impl.wsdl.WsdlOperation
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlRequest
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.model.iface.Interface
import com.eviware.soapui.model.iface.Operation
import com.eviware.soapui.model.testsuite.TestCase
import com.eviware.soapui.model.testsuite.TestSuite

interface SoapUiVisitor {

    class Environment {

    }

    fun enterProject(env: Environment, project: WsdlProject)

    fun exitProject()

    fun unsupportedInterface(env: Environment, interfaze: Interface)

    fun enterInterface(env: Environment, interfaze: Interface)

    fun exitInterface(env: Environment, interfaze: WsdlInterface)

    fun unsupportedOperation(operation: Operation?)
    fun enterOperation(operation: Environment, operation1: WsdlOperation)
    fun request(env: Environment, wsdlRequest: WsdlRequest)
    fun unsupportedTestSuite(env: SoapUiVisitor.Environment, suite: TestSuite)
    fun enterTestSuite(env: SoapUiVisitor.Environment, suite: TestSuite?)
    fun exitTestSuite(env: SoapUiVisitor.Environment, suite: TestSuite?)
    fun unsupportedTestCase(env: SoapUiVisitor.Environment, testCase: TestCase): Any
    fun enterTestCase(env: SoapUiVisitor.Environment, testCase: WsdlTestCase)
    fun exitTestCase(env: SoapUiVisitor.Environment, testCase: WsdlTestCase)

}