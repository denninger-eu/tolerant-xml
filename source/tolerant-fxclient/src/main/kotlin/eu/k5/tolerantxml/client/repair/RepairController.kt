package eu.k5.tolerantxml.client.repair

import eu.k5.tolerant.converter.TolerantConverterRequest
import tornadofx.Controller
import tornadofx.onChange

class RepairController() : Controller() {

    override val scope = super.scope as RepairScope
    val model = scope.model

    init {
        model.input.onChange { repair(it) }
    }

    fun repair(text: String?) {
        if (text == null) {
            return
        }

        val request = TolerantConverterRequest()
        request.content = text

        val result = scope.converter.convert(request)
        if (result.content != null) {
            model.repaired.set(result.content!!)
        } else {
            model.repaired.set(result.error ?: "")
        }
    }
}