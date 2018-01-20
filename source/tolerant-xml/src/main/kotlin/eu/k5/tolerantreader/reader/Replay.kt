package eu.k5.tolerantreader.reader

import javax.xml.namespace.QName
import javax.xml.stream.events.XMLEvent

class Replay(val qName: QName) {


    val subreplay = ArrayList<Replay>()

    var replayText: String? = null


    fun asStreamReader(): TolerantStreamReader {
        return ReplayStream(this)
    }

}

class ReplayStream(private val replay: Replay) : TolerantStreamReader() {
    override fun hasNext(): Boolean {
        return index < 2
    }

    private var index = 0


    override fun getText(): String? {
        return replay.replayText
    }

    override fun next(): Int {
        if (index == 0) {
            index++
            return XMLEvent.CHARACTERS
        } else {
            index++
            return XMLEvent.END_ELEMENT
        }
    }

}