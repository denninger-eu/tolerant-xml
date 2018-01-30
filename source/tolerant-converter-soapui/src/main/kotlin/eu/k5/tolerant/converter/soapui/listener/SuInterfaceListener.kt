package eu.k5.tolerant.converter.soapui.listener

import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.impl.wsdl.WsdlOperation
import com.eviware.soapui.impl.wsdl.WsdlRequest
import com.eviware.soapui.model.iface.Interface
import com.eviware.soapui.model.iface.Operation

interface SuInterfaceListener {
    fun unsupportedInterface(env: Environment, interfaze: Interface)

    fun enterInterface(env: Environment, interfaze: Interface)

    fun exitInterface(env: Environment, interfaze: WsdlInterface)

    fun unsupportedOperation(env: Environment, operation: Operation)
    fun enterOperation(env: Environment, operation1: WsdlOperation)
    fun exitOperation(env: Environment, operation: WsdlOperation)
    fun request(env: Environment, wsdlRequest: WsdlRequest)

}