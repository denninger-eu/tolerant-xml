package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.BindContext
import eu.k5.tolerantreader.InitContext
import eu.k5.tolerantreader.tolerant.TolerantSchema
import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.binding.TolerantWriter
import org.w3c.dom.Node
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory

class DomWriter : TolerantWriter {
    override fun createAttributeAssigner(initContext: InitContext, attribute: QName, name: String, typeName: QName): Assigner {
        return DomAttributeAssigner(attribute)
    }

    override fun rootAssigner(elementName: QName): Assigner {
        return DomRootAssigner(elementName)
    }

    override fun createContext(schema: TolerantSchema): BindContext {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        return BindContext(schema, DomRoot(documentBuilder.newDocument()))
    }

    override fun createElementAssigner(initContext: InitContext, base: QName, element: QName, target: QName, list: Boolean, weight: Int): Assigner {
        return if (target.localPart == "string") {
            DomTextContentAssigner(element, weight)
        } else {
            DomElementAssigner(element, weight)
        }
    }

    override fun createSupplier(typeName: QName): (elementName: QName) -> Any {
        return { DomValue(it, typeName) }
    }


}


abstract class DomNode(val weight: Int) {


    abstract fun asNode(context: DomContext): Node
}


class DomValue(name: QName, val typeName: QName) {
    val attributes: MutableList<DomAttribute> = ArrayList()
    var element: DomElement? = null
}

class DomAttribute(val attributeName: QName, val value: String?, val weight: Int) {

}

class DomElement(val elementName: QName, val typeName: QName, weight: Int) : DomNode(weight) {

    val elements: MutableList<DomNode> = ArrayList()

    val attributes: MutableList<DomAttribute> = ArrayList()

    override fun asNode(context: DomContext): Node {

        elements.sortBy { it.weight }
        val nodes = ArrayList<Node>()
        elements.mapTo(nodes) { it.asNode(context) }

        val element = context.document.createElementNS(elementName.namespaceURI, elementName.localPart)

        attributes.sortBy { it.weight }

        for (attribute in attributes) {
            element.setAttributeNS(attribute.attributeName.namespaceURI, attribute.attributeName.localPart, attribute.value)

        }


        nodes.forEach {
            element.appendChild(it)
        }

        return element
    }
}

class DomTextContent(val elementName: QName, val value: String, weight: Int) : DomNode(weight) {
    override fun asNode(context: DomContext): Node {
        val textNode = context.document.createTextNode(value)
        val element = context.document.createElementNS(elementName.namespaceURI, elementName.localPart)
        element.appendChild(textNode)
        return element
    }

}

class DomRootAssigner(val elementName: QName) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomRoot) {
            if (value is DomValue) {
                val domElement = DomElement(elementName, value.typeName, 0)
                domElement.attributes.addAll(value.attributes)
                instance.addRootElement(domElement)
                value.element = domElement
            }
        }
    }
}

class DomTextContentAssigner(var elementName: QName, val weight: Int) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            if (value != null) {
                val element = instance.element
                element?.elements?.add(DomTextContent(elementName, value.toString(), weight))

            }
        } else {
            TODO("unsupported")
        }
    }

}

class DomElementAssigner(val element: QName, val weight: Int) : Assigner {

    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            if (value is DomValue) {

                val domElement = DomElement(element, value.typeName, weight)

                instance.element!!.elements.add(domElement)
                instance.element!!.attributes.addAll(value.attributes)
                value.element = domElement
            }

        }
    }

}


class DomAttributeAssigner(private val element: QName) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            instance.attributes.add(DomAttribute(element, value?.toString(), 0))
        }
    }

}