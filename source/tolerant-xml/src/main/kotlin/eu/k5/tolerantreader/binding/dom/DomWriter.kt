package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.*
import eu.k5.tolerantreader.binding.*
import eu.k5.tolerantreader.tolerant.TolerantSchema
import eu.k5.tolerantreader.binding.model.NoOpAssigner
import eu.k5.tolerantreader.binding.model.ReflectionUtils
import eu.k5.tolerantreader.tolerant.IdRefType
import eu.k5.tolerantreader.tolerant.XSI_NAMESPACE
import org.w3c.dom.Node
import java.util.*
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory


class DomWriter : TolerantWriter {
    override fun createCloser(initContext: InitContext): Closer {
        return DomElementCloser()
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

    override fun createEnumSupplier(initContext: InitContext,
                                    enumName: QName,
                                    literals: Collection<String>):
            EnumSupplier {

        return EnumSupplier(xsString) { content: BindContext, value: String
            ->
            if (literals.contains(value)) {
                value
            } else {
                content.addViolation(Violation.INVALID_ENUM_LITERAL, value)
                null
            }
        }

    }


    override fun rootAssigner(elementName: QName): Assigner {
        return DomRootAssigner(elementName)
    }

    override fun createContext(schema: TolerantSchema, readerConfig: TolerantReaderConfiguration): BindContext {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        return BindContext(schema, DomRoot(documentBuilder.newDocument()), readerConfig)
    }

    override fun createElementAssigner(initContext: InitContext, entityType: QName, element: QName, target: QName, parameters: ElementParameters): Assigner {
        if (parameters.attribute) {
            val toStringAdapter = simpleTypeAdapter[target]

            return if (toStringAdapter == null) {
                initContext.addFinding(Type.MISSING_TYPE_ADAPTER, target.toString())
                NoOpAssigner
            } else {
                DomAttributeAssigner(element, toStringAdapter)
            }
        }
        if (simpleTypeAdapter.containsKey(target)) {
            val toStringAdapter = simpleTypeAdapter[target]

            return if (toStringAdapter == null) {
                initContext.addFinding(Type.MISSING_TYPE_ADAPTER, target.toString())
                NoOpAssigner
            } else {
                DomTextContentAssigner(parameters.weight, element, toStringAdapter)
            }
        } else {

            return DomElementAssigner(element, parameters.weight, target)
        }
    }

    override fun createSupplier(initContext: InitContext, typeName: QName): (elementName: QName) -> Any {
        return { DomValue(it, typeName) }
    }


}


abstract class DomNode(val weight: Int) {

    abstract fun asNode(context: DomContext): List<Node>
}

class DomComment(private val comment: String, weight: Int) : DomNode(weight) {

    override fun asNode(context: DomContext): List<Node> {
        //    val textBefore = context.document.createTextNode(" ")
        val comment = context.document.createComment(comment)
        //   val textAfter = context.document.createTextNode("\n\t")
        return Arrays.asList(comment)
    }

}


class DomValue(name: QName, val typeName: QName) {
    val attributes: MutableList<DomAttribute> = ArrayList()
    var element: DomElement? = null
}

class DomAttribute(val attributeName: QName, val value: String?, val weight: Int) {

}

class DomElement(
        private val elementName: QName,
        private val expectedTypeName: QName,
        private val actualType: QName,
        weight: Int
) : DomNode(weight) {

    val elements: MutableList<DomNode> = ArrayList()

    val attributes: MutableList<DomAttribute> = ArrayList()

    override fun asNode(context: DomContext): List<Node> {

        val elementPrefix = context.getPrefix(elementName.namespaceURI)
        val element = context.document.createElement(elementPrefix + ":" + elementName.localPart)

        elements.sortBy { it.weight }
        val nodes = ArrayList<Node>()
        for (element in elements) {
            nodes.addAll(element.asNode(context))
        }


        attributes.sortBy { it.weight }

        if (actualType != expectedTypeName) {
            val typePrefix = context.getPrefix(actualType.namespaceURI)
            val xsiPrefix = context.getPrefix(XSI_NAMESPACE)
            element.setAttribute(xsiPrefix + ":type", typePrefix + ":" + actualType.localPart)
        }

        for (attribute in attributes) {
            val attributePrefix = context.getPrefix(attribute.attributeName.namespaceURI)
            element.setAttribute(attributePrefix + ":" + attribute.attributeName.localPart, attribute.value)
        }

        nodes.forEach {
            element.appendChild(it)
        }
        return Arrays.asList(element)
    }
}

class DomTextContent(
        weight: Int,
        private val elementName: QName,
        private val value: String
) : DomNode(weight) {

    override fun asNode(context: DomContext): List<Node> {
        val textNode = context.document.createTextNode(value)

        val prefix = context.getPrefix(elementName.namespaceURI)

        val element = context.document.createElement(prefix + ":" + elementName.localPart)
        element.appendChild(textNode)
        return Arrays.asList(element)
    }

}

class DomRootAssigner(
        private val elementName: QName
) : Assigner {
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

class DomTextContentAssigner(
        private val weight: Int,
        private var elementName: QName,
        private val toString: (Any) -> String
) : Assigner {

    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            if (value != null) {

                context.retrieveComments().mapTo(instance.element!!.elements) {
                    DomComment(it, weight)
                }

                val element = instance.element
                element?.elements?.add(DomTextContent(weight, elementName, toString(value)))
            }
        } else {
            TODO("unsupported")
        }
    }

}

class DomElementAssigner(
        private val element: QName,
        private val weight: Int,
        private val target: QName
) : Assigner {

    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            if (value is DomValue) {


                val comments = context.retrieveComments()

                val domElement = DomElement(element, target, value.typeName, weight)
                domElement.attributes.addAll(value.attributes)

                comments.mapTo(instance.element!!.elements) {
                    DomComment(it, weight)
                }
                instance.element!!.elements.add(domElement)
                value.element = domElement
                return
            }
        }
        TODO("Different types")
    }

}


class DomAttributeAssigner(
        private val element: QName,
        private val toStringAdapter: (Any) -> String
) : Assigner {
    override fun assign(context: BindContext, instance: Any, value: Any?) {
        if (instance is DomValue) {
            if (value != null) {
                instance.attributes.add(DomAttribute(element, toStringAdapter(value), 0))
            }
        }
    }
}

class DomElementCloser : Closer {

    override fun close(bindContext: BindContext, instance: Any) {
        if (instance is DomValue) {
            val comments = bindContext.retrieveComments()

            comments.mapTo(instance.element!!.elements) {
                DomComment(it, Int.MAX_VALUE)
            }

        }
    }
}