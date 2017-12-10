package eu.k5.tolerantreader

import eu.k5.tolerantreader.strict.StrictSchema
import javax.xml.stream.XMLStreamWriter


class StrictWriter(private val schema: StrictSchema) {


    fun write(instance: Any, writer: XMLStreamWriter) {

        val type = schema.get(instance.javaClass)

        type.write(instance, writer)


    }

}