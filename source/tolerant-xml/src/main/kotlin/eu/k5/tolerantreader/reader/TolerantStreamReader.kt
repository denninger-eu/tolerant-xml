package eu.k5.tolerantreader.reader

import javax.xml.namespace.NamespaceContext
import javax.xml.namespace.QName
import javax.xml.stream.Location
import javax.xml.stream.XMLStreamReader

abstract class TolerantStreamReader : XMLStreamReader{
    override fun getPIData(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCharacterEncodingScheme(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEncoding(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getName(): QName {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributePrefix(index: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNamespacePrefix(index: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTextLength(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getProperty(name: String?): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun isStandalone(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isAttributeSpecified(index: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributeType(index: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEventType(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVersion(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPITarget(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocalName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun nextTag(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEndElement(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isCharacters(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributeCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNamespaceURI(prefix: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNamespaceURI(index: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNamespaceURI(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocation(): Location {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributeLocalName(index: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNamespaceCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNamespaceContext(): NamespaceContext {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTextCharacters(): CharArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTextCharacters(sourceStart: Int, target: CharArray?, targetStart: Int, length: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun require(type: Int, namespaceURI: String?, localName: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributeNamespace(index: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getElementText(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributeValue(namespaceURI: String?, localName: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributeValue(index: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isStartElement(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun standaloneSet(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPrefix(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasName(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributeName(index: Int): QName {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isWhiteSpace(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasText(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTextStart(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}