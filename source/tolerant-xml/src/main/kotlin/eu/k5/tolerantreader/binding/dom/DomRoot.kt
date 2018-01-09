package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.RootElement
import org.w3c.dom.Document
import org.w3c.dom.Element

class DomRoot(private val document: Document) : RootElement {

    private var rootElement: DomElement? = null

    fun addRootElement(element: DomElement) {
        rootElement = element
    }

    override fun seal(bindContext: BindContext): Any {

        bindContext.queryConfiguration(NamespaceStrategy::class.java)

        val context = DomContext(document, bindContext.readerConfig)
        val root = rootElement!!.asNode(context) as Element

        for ((namespace, prefix) in context.getUsedNamespaces()) {
            root.setAttribute("xmlns:" + prefix, namespace)
        }

        document.appendChild(root)
        return document
    }

}