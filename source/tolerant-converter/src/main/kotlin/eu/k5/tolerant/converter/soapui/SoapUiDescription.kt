package eu.k5.tolerant.converter.soapui

data class SoapUiDescription(
        var name: String? = null,

        var interfaces: MutableList<SoapUiInterface>? = ArrayList(),

        var suites: MutableList<SoapUiTestSuite>? = ArrayList()
)

data class SoapUiInterface(
        var name: String? = null,
        var wsdl: String? = null,

        var requests: MutableList<SoapUiRequest>? = ArrayList()

)

data class SoapUiRequest(
        var name: String? = null,
        var operation: String? = null
)

data class SoapUiTestSuite(
        var name: String? = null,

        var supported: Boolean? = null,

        var cases: MutableList<SoapUiTestCase>? = ArrayList()
)

data class SoapUiTestCase(
        var name: String? = null,

        var steps: MutableList<SoapUiTestStep>? = ArrayList(),

        var supported: Boolean? = null
)

data class SoapUiTestStep(
        var step: String? = null
)


