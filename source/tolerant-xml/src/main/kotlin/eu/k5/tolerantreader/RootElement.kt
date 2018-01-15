package eu.k5.tolerantreader

import javax.xml.stream.XMLStreamReader

interface RootElement {

    fun seal(context: BindContext): Any?

    fun pushFrameElement(context: BindContext, stream: XMLStreamReader)

    fun popFrameElement(context: BindContext)

}