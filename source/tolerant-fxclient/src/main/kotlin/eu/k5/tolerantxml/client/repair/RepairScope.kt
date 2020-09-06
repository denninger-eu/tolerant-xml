package eu.k5.tolerantxml.client.repair

import eu.k5.tolerant.converter.TolerantConverter
import tornadofx.Scope

class RepairScope(
        val converter: TolerantConverter
) : Scope() {
    val model = RepairModel()
}