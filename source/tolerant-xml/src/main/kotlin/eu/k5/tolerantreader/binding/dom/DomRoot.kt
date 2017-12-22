package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.RootElement
import org.w3c.dom.Document
import org.w3c.dom.Element

class DomRoot(val document: Document) : RootElement {


    private var rootElement: DomElement? = null

    fun addRootElement(element: DomElement) {
        rootElement = element
    }

    override fun seal(): Any {

        val context = DomContext(document)

        val root = rootElement!!.asNode(context) as Element

        for ((namespace, prefix) in context.getUsedNamespaces()) {
            root.setAttribute("xmlns:" + prefix, namespace)
        }


        document.appendChild(root)


        return document
    }

}