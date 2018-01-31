package eu.k5.tolerant.converter.soapui

import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestStepRegistry

fun main(args: Array<String>) {

    val iterator = WsdlTestStepRegistry.getInstance().factories.iterator()
    while (iterator.hasNext()) {
        println(iterator.next().type)
    }

}