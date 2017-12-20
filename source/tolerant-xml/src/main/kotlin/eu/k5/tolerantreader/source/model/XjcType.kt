package eu.k5.tolerantreader.source.model

import javax.xml.namespace.QName

class XjcType(
        val root: Boolean,
        val type: Class<*>,
        val registry: XjcXmlRegistry,
        val qName: QName
) {}