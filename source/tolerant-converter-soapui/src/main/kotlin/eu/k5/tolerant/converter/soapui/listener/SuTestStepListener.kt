package eu.k5.tolerant.converter.soapui.listener

import com.eviware.soapui.impl.wsdl.teststeps.PropertyTransfersTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDelayTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGotoTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.model.testsuite.TestStep

interface SuTestStepListener {

    fun request(env: Environment, step: WsdlTestRequestStep)

    fun delay(env: Environment, step: WsdlDelayTestStep)

    fun transfer(env: Environment, step: PropertyTransfersTestStep)

    fun gotoStep(env: Environment, step: WsdlGotoTestStep)

    fun unsupported(env: Environment, step: TestStep) {

    }

}