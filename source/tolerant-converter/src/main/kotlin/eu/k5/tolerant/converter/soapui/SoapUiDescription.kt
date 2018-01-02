package eu.k5.tolerant.converter.soapui

data class SoapUiDescription(
        var name: String? = null,

        var interfaces: List<SoapUiInterface>? = ArrayList(),

        var requests: List<SoapUiRequest>? = ArrayList(),

        var suites: List<SoapUiTestSuite>? = ArrayList()
)

data class SoapUiInterface(
        var name: String? = null
)

data class SoapUiRequest(
        var name: String? = null
)

data class SoapUiTestSuite(
        var name: String? = null,

        var supported: Boolean? = null,

        var cases: List<SoapUiTestCase>? = ArrayList()
)

data class SoapUiTestCase(
        var name: String? = null,

        var steps: List<SoapUiTestStep>? = ArrayList()
)

data class SoapUiTestStep(
        var step: String? = null
)


