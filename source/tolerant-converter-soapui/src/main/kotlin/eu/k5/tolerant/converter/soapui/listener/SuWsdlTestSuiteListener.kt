package eu.k5.tolerant.converter.soapui.listener

import com.eviware.soapui.impl.wsdl.WsdlTestSuite
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.model.testsuite.TestCase
import com.eviware.soapui.model.testsuite.TestSuite

interface SuWsdlTestSuiteListener {

    fun unsupportedTestSuite(env: Environment, suite: TestSuite)
    fun enterTestSuite(env: Environment, suite: WsdlTestSuite)
    fun exitTestSuite(env: Environment, suite: WsdlTestSuite)
    fun unsupportedTestCase(env: Environment, testCase: TestCase)
    fun enterTestCase(env: Environment, testCase: WsdlTestCase)
    fun exitTestCase(env: Environment, testCase: WsdlTestCase)

    fun createTestStepListener(): SuTestStepListener
}