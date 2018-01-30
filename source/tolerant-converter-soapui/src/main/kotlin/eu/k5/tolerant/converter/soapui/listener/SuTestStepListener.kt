package eu.k5.tolerant.converter.soapui.listener

import com.eviware.soapui.impl.wsdl.teststeps.WsdlDelayTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep

interface SuTestStepListener {

    fun request(env: Environment, step: WsdlTestRequestStep)

    fun delay(env: Environment, step: WsdlDelayTestStep)


}