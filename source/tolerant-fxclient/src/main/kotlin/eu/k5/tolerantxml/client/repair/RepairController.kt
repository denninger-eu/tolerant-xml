package eu.k5.tolerantxml.client.repair

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

        model.repaired.set(text)
    }
}