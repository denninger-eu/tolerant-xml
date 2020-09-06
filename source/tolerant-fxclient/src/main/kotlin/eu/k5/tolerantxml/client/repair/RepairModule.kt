package eu.k5.tolerantxml.client.repair

import eu.k5.soapui.fx.NewTabEvent
import eu.k5.soapui.fx.SoapUiModule
import javafx.scene.control.Tab
import tornadofx.Component
import tornadofx.find

class RepairModule : Component(), SoapUiModule {
    override val name: String = "tolerantrepair"

    override fun init() {

        subscribe<CreateTolerantConverter> {
            startTab(it)

        }
    }

    private fun startTab(event: CreateTolerantConverter) {
        println("Event received")

        println(event.xsds)

        val scope = RepairScope()

        val view = find<RepairView>(scope)

        val tab = Tab("Repair " + event.name)
        tab.contentProperty().set(view.root)
        fire(NewTabEvent(tab))
    }
}