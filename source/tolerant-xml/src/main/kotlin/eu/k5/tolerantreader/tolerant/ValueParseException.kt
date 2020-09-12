package eu.k5.tolerantreader.tolerant

import eu.k5.tolerantreader.reader.ViolationType
import java.lang.RuntimeException

class ValueParseException(
        val violation: ViolationType,
        val rawXml: String,
        message: String
) : RuntimeException(message) {

}
