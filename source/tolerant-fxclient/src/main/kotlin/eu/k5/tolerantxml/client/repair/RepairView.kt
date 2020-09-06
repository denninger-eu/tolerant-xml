package eu.k5.tolerantxml.client.repair

import tornadofx.*

class RepairView(

) : View() {

    override val scope = super.scope as RepairScope
    val controller: RepairController = find<RepairController>(scope)
    val model = scope.model

    override val root = splitpane() {

        scrollpane {
            this += XmlTextArea.newCodeArea { model.input.bind(it.textProperty()) }
        }

        scrollpane {
            this += XmlTextArea.newCodeArea {
                model.repaired.addListener { _, _, newText -> it.replaceText(0, it.textProperty().value.length, newText) }
            }
        }

/*
        textarea {
            bind(model.input)

        }
        textarea {

            bind(model.repaired)
        }
*/
    }


}