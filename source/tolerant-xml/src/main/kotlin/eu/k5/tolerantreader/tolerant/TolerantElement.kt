package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.binding.Assigner
import eu.k5.tolerantreader.binding.Retriever
import javax.xml.namespace.QName

class TolerantElement(
        val qname: QName,
        val type: TolerantType,
        val assigner: Assigner,
        val retriever: Retriever,
        val attribute: Boolean
)