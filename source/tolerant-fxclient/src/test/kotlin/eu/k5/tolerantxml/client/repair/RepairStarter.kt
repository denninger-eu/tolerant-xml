package eu.k5.tolerantxml.client.repair

import javafx.scene.Parent
import tornadofx.App
import tornadofx.View
import tornadofx.borderpane

fun main(args: Array<String>) {
    tornadofx.launch<RepairStarter>(*args)
}

class RepairStarter : App(RepairStarterView::class) {

    val view: RepairView by inject()

    init {

    }
}

class RepairStarterView() : View() {
    override val root = borderpane { }

}