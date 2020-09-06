package eu.k5.tolerantxml.client.repair

import eu.k5.tolerant.converter.config.ReaderConfig
import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import javafx.scene.Parent
import tornadofx.App
import tornadofx.View
import tornadofx.borderpane
import tornadofx.find

fun main(args: Array<String>) {
    tornadofx.launch<RepairStarter>(*args)
}

class RepairStarter : App(RepairStarterView::class) {

    val view: RepairStarterView by inject()

    init {
        val scope = RepairScope()
        scope.model.input.set("Test value")
        view.root.center = find<RepairView>(scope).root
    }

    companion object {
        fun initConverter() {

            val reader = ReaderConfig()
            reader.xsdContent

            //val config = TolerantConverterConfiguration()

        }
    }
}

class RepairStarterView() : View() {
    override val root = borderpane { }
}

