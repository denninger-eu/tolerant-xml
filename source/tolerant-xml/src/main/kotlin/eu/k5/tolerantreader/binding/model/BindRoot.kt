package eu.k5.tolerantreader.binding.model

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.RootElement
import javax.xml.stream.XMLStreamReader

class BindRoot : RootElement {
    override fun pushFrameElement(context: BindContext, stream: XMLStreamReader) {
    }
    override fun popFrameElement(context: BindContext) {
    }


    var instance: Any? = null

    override fun seal(context: BindContext): Any?
            = instance


}