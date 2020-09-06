package eu.k5.tolerantxml.client.repair

import tornadofx.FXEvent

class CreateTolerantConverter(
        val name: String,
        val mainPart: String
) : FXEvent() {
    val xsds: MutableMap<String, String> = HashMap()
}