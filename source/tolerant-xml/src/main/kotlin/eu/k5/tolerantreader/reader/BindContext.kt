package eu.k5.tolerantreader.reader

import eu.k5.tolerantreader.ReaderContext
import eu.k5.tolerantreader.RootElement
import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.tolerant.*
import org.slf4j.LoggerFactory
import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamReader
import kotlin.collections.HashMap


class OpenReference(
        val id: String,
        val instance: Any,
        val assigner: Assigner
)

class BindContext(
        private val schema: TolerantSchema,
        private val root: RootElement,
        val readerConfig: TolerantReaderConfiguration
) : ReaderContext {
    private val violations: MutableList<Violation> = ArrayList()

    private val trackComments: Boolean = true

    private val names: Deque<NameFrame> = ArrayDeque()
    private val elements: Deque<TolerantElement> = ArrayDeque()
    private val types: Deque<TolerantType> = ArrayDeque()
    private val instances: Deque<Any> = ArrayDeque()
    private val openReferences: MutableList<OpenReference> = ArrayList()
    private val comments: MutableList<String> = ArrayList()


    init {
        instances.push(root)
    }

    fun postProcess() {
        for (open in openReferences) {
            val entity = getEntityById(open.id)
            if (entity != null) {
                open.assigner.assign(this, open.instance, entity)
            } else {
                addViolation(ViolationType.MISSING_ENTITY, "")
            }

        }
    }

    fun isEmpty(): Boolean = elements.isEmpty()


    fun getElement(namespaceURI: String?, localName: String?): TolerantElement? {
        val current = types.peek()
        if (current is TolerantComplexType) {
            return current.getElement(namespaceURI, localName)
        } else if (current is TolerantComplexProxy) {
            return current.getElement(namespaceURI, localName)
        }
        return null
    }

    fun pushName(qName: QName) {
        val current = NameFrame(qName)
        val lastName = names.peek()
        if (lastName != null) {
            val replay = lastName.replays?.remove(qName.localPart)
            if (replay != null) {
                current.pushChildren(replay)
            }
        }

        names.push(current)

    }

    fun push(element: TolerantElement, instance: Any, type: TolerantType) {
        elements.push(element)
        types.push(type)
        instances.push(instance)
    }

    fun pop() {
        elements.pop()
        val instance = instances.pop()
        val type = types.pop()

        type.closeType(this, instance)
    }


    fun getCurrentInstance(): Any? = instances.peek()


    fun sealedRoot(): Any? = root.seal(this)

    override fun getComplexType(name: QName): TolerantType {
        return schema.getComplexType(name)
    }

    override fun addViolation(type: ViolationType, description: String) {
        logger.warn("{}: {}", type, description)
        violations.add(Violation(type, description, ""))
    }

    companion object {
        val logger = LoggerFactory.getLogger(BindContext::class.java)
    }

    private val entities: MutableMap<String, Any> = HashMap()

    override fun getEntityById(id: String): Any? = entities[id]


    override fun addOpenIdRef(id: String, instance: Any, delegate: Assigner) {
        openReferences.add(OpenReference(id, instance, delegate))
    }

    override fun registerEntity(value: String, instance: Any) {
        entities.put(value, instance)
    }

    fun addComment(comment: String) {
        if (trackComments) {
            comments.add(comment)
        }
    }

    override fun retrieveComments(): List<String> {
        if (!trackComments) {
            return comments
        }
        val retrieved = ArrayList(comments)
        comments.clear()
        return retrieved
    }


    fun pushFrameElement(stream: XMLStreamReader) {
        root.pushFrameElement(this, stream)
    }

    fun keepFrame(): Boolean {
        return true
    }

    fun popFrame() {
        root.popFrameElement(this)
    }

    fun keepCharacters(): Boolean = keepFrame() && isEmpty()

    fun addCharacters(elementText: String) {
        root.addCharacters(elementText)
    }

    fun createResult(): ReadResult {
        val instance = sealedRoot()

        return ReadResult(violations, instance)
    }

    fun getTransformer(localName: String): TolerantTransformer? {
        val current = types.peek() ?: return null

        val qName = current.getQualifiedName()

        val transformer = schema.getTransformer(qName, localName)

        return transformer

    }

    fun pushReplay(replay: Replay) {
        val nameFrame = names.peek()

        nameFrame.pushReplay(replay)


    }

    fun popName() {
        names.pop()
    }

    fun retrieveReplay(): Map<String, Replay>? {
        val replays = names.peek().replays
        names.peek().replays = null
        return replays
    }

}

class NameFrame(
        val name: QName
) {

    var replays: MutableMap<String, Replay>? = null

    fun pushReplay(replay: Replay) {
        if (replays == null) {
            replays = HashMap()
        }
        replays!!.put(replay.qName.localPart, replay)
    }

    fun pushChildren(replay: Replay) {
        for (child in replay.children) {
            pushReplay(child)
        }
    }

}