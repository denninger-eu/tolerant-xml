package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.binding.Assigner
import javax.xml.namespace.QName

class TolerantElement(val qname: QName, val type: TolerantType, val assigner: Assigner, val attribute: Boolean) {


}