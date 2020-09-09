package eu.k5.tolerantxml.client.repair

import javafx.scene.control.ScrollPane
import tornadofx.*

class RepairView(

) : View() {

    override val scope = super.scope as RepairScope
    val controller: RepairController = find<RepairController>(scope)
    val model = scope.model

    override val root = splitpane() {

        scrollpane {
            this += XmlTextArea.newCodeArea {
                model.input.bind(it.textProperty())
                it.prefHeightProperty().bind(this.heightProperty())
                it.prefWidthProperty().bind(this.widthProperty())
            }
            hbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
            vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
            this.prefHeightProperty().bind(this@splitpane.heightProperty())
            this.prefWidthProperty().bind(this@splitpane.widthProperty())
        }

        scrollpane {
            this += XmlTextArea.newCodeArea {
                model.repaired.addListener { _, _, newText -> it.replaceText(0, it.textProperty().value.length, newText) }
                it.prefHeightProperty().bind(this.heightProperty())
                it.prefWidthProperty().bind(this.widthProperty())
            }
            hbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
            vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
            this.prefHeightProperty().bind(this@splitpane.heightProperty())
            this.prefWidthProperty().bind(this@splitpane.widthProperty())
        }

    }


}