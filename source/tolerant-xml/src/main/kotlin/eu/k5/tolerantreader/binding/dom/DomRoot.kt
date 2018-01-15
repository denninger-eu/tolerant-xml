package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.RootElement
import eu.k5.tolerantreader.tolerant.TolerantSimpleType
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamReader

class DomRoot(private val document: Document) : RootElement {

    private var frame: Deque<DomElement> = ArrayDeque()

    private var rootElement: DomElement? = null

    fun addRootElement(element: DomElement) {
        if (frame.isEmpty()) {
            rootElement = element
        } else {
            frame.peek().elements.add(element)
        }
    }

    override fun seal(bindContext: BindContext): Any? {

        val rootElement = rootElement ?: return null

        bindContext.queryConfiguration(NamespaceStrategy::class.java)

        val sealContext = DomSealContext(document, bindContext.readerConfig)
        val root = rootElement.asNode(sealContext)[0] as Element

        for ((namespace, prefix) in sealContext.getUsedNamespaces()) {
            root.setAttribute("xmlns:" + prefix, namespace)
        }

        document.appendChild(root)
        return document
    }

    override fun pushFrameElement(context: BindContext, stream: XMLStreamReader) {
        val localName1 = stream.localName
        val namespaceURI = stream.namespaceURI

        val qName = QName(namespaceURI, localName1)
        val domElement = DomElement(qName, qName, qName, 0)


        for (index in 0 until stream.attributeCount) {
            val namespace = stream.getAttributeNamespace(index)
            val localName = stream.getAttributeLocalName(index)
            val value = stream.getAttributeValue(index)
            val domAttribute = DomAttribute(QName(namespace, localName), value, index)
            domElement.attributes.add(domAttribute)
        }

        if (frame.isEmpty()) {
            rootElement = domElement
        } else {
            val current = frame.peek().elements
            context.retrieveComments().mapTo(current) {
                DomComment(it, 0)
            }


            current.add(domElement)
        }
        frame.push(domElement)
    }

    override fun popFrameElement(context: BindContext) {
        frame.pop()
    }
}

