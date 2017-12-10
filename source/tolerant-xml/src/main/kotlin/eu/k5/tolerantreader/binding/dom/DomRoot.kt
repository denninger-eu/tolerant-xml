package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.RootElement
import org.w3c.dom.Document

class DomRoot(val document: Document) : RootElement {


    private var rootElement: DomElement? = null

    fun addRootElement(element: DomElement) {
        rootElement = element
    }

    override fun seal(): Any {

        val context = DomContext(document)

        val node = rootElement!!.asNode(context)

        document.appendChild(node)


        return document
    }

}