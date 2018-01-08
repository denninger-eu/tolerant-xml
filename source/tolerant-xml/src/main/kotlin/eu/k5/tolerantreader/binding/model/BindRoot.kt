package eu.k5.tolerantreader.binding.model

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.RootElement

class BindRoot : RootElement {

    var instance: Any? = null

    override fun seal(context: BindContext): Any = instance!!


}