package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.*
import eu.k5.tolerantreader.tolerant.TolerantSchema
import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.binding.ElementParameters
import eu.k5.tolerantreader.binding.TolerantWriter
import eu.k5.tolerantreader.binding.model.ReflectionUtils
import eu.k5.tolerantreader.tolerant.IdRefType
import eu.k5.tolerantreader.tolerant.XSI_NAMESPACE
import eu.k5.tolerantreader.tolerant.XSI_TYPE
import org.w3c.dom.Node
import java.util.*
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory


class DomWriter : TolerantWriter {
    override fun createEnumSupplier(initContext: InitContext, enumName: QName): (BindContext, String) -> Any {

// FIXME check range of value
        return { content: BindContext, value: String -> value }

    }

    private val utils = ReflectionUtils()
    private val simpleTypeAdapter = HashMap<QName, (Any) -> String>()

    init {
        simpleTypeAdapter.put(xsString) {
            it.toString()
        }
        simpleTypeAdapter.put(xsBase64Binary) {
            Base64.getEncoder().encodeToString(it as ByteArray)
        }
        simpleTypeAdapter.put(xsBoolean) {
            it.toString()
        }
        simpleTypeAdapter.put(xsHexBinary) {
            Base64.getEncoder().encodeToString(it as ByteArray)
        }
        simpleTypeAdapter.put(xsQname) {
            it.toString()
        }
        simpleTypeAdapter.put(xsDate) {
            it.toString()
        }
        simpleTypeAdapter.put(xsDatetime) {
            it.toString()
        }
        simpleTypeAdapter.put(xsDuration) {
            it.toString()
        }
        simpleTypeAdapter.put(xsGDay) {
            it.toString()
        }
        simpleTypeAdapter.put(xsGMonth) {
            it.toString()
        }
        simpleTypeAdapter.put(xsGMonthDay) {
            it.toString()
        }
        simpleTypeAdapter.put(xsGYear) {
            it.toString()
        }
        simpleTypeAdapter.put(xsGYearMonth) {
            it.toString()
        }
        simpleTypeAdapter.put(xsTime) {
            it.toString()
        }


        simpleTypeAdapter.put(xsByte) {
            it.toString()
        }
        simpleTypeAdapter.put(xsDecimal) {
            it.toString()
        }
        simpleTypeAdapter.put(xsInt) {
            it.toString()
        }
        simpleTypeAdapter.put(xsInteger) {
            it.toString()
        }
        simpleTypeAdapter.put(xsLong) {
            it.toString()
        }
        simpleTypeAdapter.put(xsNegativeInteger) {
            it.toString()
        }
        simpleTypeAdapter.put(xsNonNegativeInteger) {
            it.toString()
        }
        simpleTypeAdapter.put(xsNonPositiveInteger) {
            it.toString()
        }
        simpleTypeAdapter.put(xsPositiveInteger) {
            it.toString()
        }
        simpleTypeAdapter.put(xsShort) {
            it.toString()
        }
        simpleTypeAdapter.put(xsUnsignedLong) {
            it.toString()
        }
        simpleTypeAdapter.put(xsUnsignedInt) {
            it.toString()
        }
        simpleTypeAdapter.put(xsUnsignedShort) {
            it.toString()
        }
        simpleTypeAdapter.put(xsUnsignedByte) {
            it.toString()
        }
        simpleTypeAdapter.put(xsDouble) {
            it.toString()
        }
        simpleTypeAdapter.put(xsFloat) {
            it.toString()
        }

        simpleTypeAdapter.put(xsIdRef) {
            (it as IdRefType).id
        }

        simpleTypeAdapter.put(xsId) {
            it.toString()
        }
    }


    override fun rootAssigner(elementName: QName): Assigner {
        return DomRootAssigner(elementName)
    }

    override fun createContext(schema: TolerantSchema): BindContext {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        return BindContext(schema, DomRoot(documentBuilder.newDocument()))
    }

    override fun createElementAssigner(initContext: InitContext, base: QName, element: QName, target: QName, parameter: ElementParameters): Assigner {
        if (parameter.attribute) {
            return DomAttributeAssigner(element)
        }
        if (simpleTypeAdapter.containsKey(target)) {
            return DomTextContentAssigner(element, parameter.weight, simpleTypeAdapter.get(target)!!)
        } else {

            return DomElementAssigner(element, parameter.weight, target)
        }
    }

    override fun createSupplier(initContext: InitContext, typeName: QName): (elementName: QName) -> Any {
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

<<<<<<< HEAD
class DomElement(private val elementName: QName, private val typeName: QName, weight: Int) : DomNode(weight) {
=======
class DomElement(val elementName: QName, val expectedTypeName: QName, val actualType: QName, weight: Int) : DomNode(weight) {
>>>>>>> a56fd5910afbe35e9f8b113fdd3907f9d87a064b

    val elements: MutableList<DomNode> = ArrayList()

    val attributes: MutableList<DomAttribute> = ArrayList()

    override fun asNode(context: DomContext): Node {

        elements.sortBy { it.weight }
        val nodes = ArrayList<Node>()
        elements.mapTo(nodes) { it.asNode(context) }

        val element = context.document.createElementNS(elementName.namespaceURI, elementName.localPart)

        attributes.sortBy { it.weight }

        if (actualType != expectedTypeName) {
            element.setAttributeNS(XSI_NAMESPACE, "type", actualType.localPart)
        }

        for (attribute in attributes) {
            element.setAttributeNS(attribute.attributeName.namespaceURI, attribute.attributeName.localPart, attribute.value)
        }

        nodes.forEach {
            element.appendChild(it)
        }
        return element
    }
}

class DomTextContent(private val elementName: QName, private val value: String, weight: Int) : DomNode(weight) {
    override fun asNode(context: DomContext): Node {
        val textNode = context.document.createTextNode(value)
        val element = context.document.createElementNS(elementName.namespaceURI, elementName.localPart)
        element.appendChild(textNode)
        return element
    }

}

class DomRootAssigner(private val elementName: QName) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomRoot) {
            if (value is DomValue) {
                val domElement = DomElement(elementName, value.typeName, value.typeName, 0)
                domElement.attributes.addAll(value.attributes)
                instance.addRootElement(domElement)
                value.element = domElement
            }
        }
    }
}

class DomTextContentAssigner(private var elementName: QName, val weight: Int, private val toString: (Any) -> String) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            if (value != null) {
                val element = instance.element
                element?.elements?.add(DomTextContent(elementName, toString(value), weight))
            }
        } else {
            TODO("unsupported")
        }
    }

}

class DomElementAssigner(val element: QName, val weight: Int, private val target: QName) : Assigner {

    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            if (value is DomValue) {

                val domElement = DomElement(element, target, value.typeName, weight)
                domElement.attributes.addAll(value.attributes)

                instance.element!!.elements.add(domElement)
                value.element = domElement
                return
            }
        }
        TODO("Different types")
    }

}


class DomAttributeAssigner(private val element: QName) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            instance.attributes.add(DomAttribute(element, value?.toString(), 0))
        }
    }

}