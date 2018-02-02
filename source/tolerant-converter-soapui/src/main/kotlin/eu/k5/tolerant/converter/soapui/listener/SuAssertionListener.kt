package eu.k5.tolerant.converter.soapui.listener

import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.XPathContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapResponseAssertion
import com.eviware.soapui.model.testsuite.TestAssertion

interface SuAssertionListener {

    fun xpathContains(env: Environment, assertion: XPathContainsAssertion)

    fun soapFault(env: Environment, assertion: SoapFaultAssertion)

    fun soapResponse(env: Environment, assertion: SoapResponseAssertion)

    fun unsupported(env: Environment, assertion: TestAssertion)

}