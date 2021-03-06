package eu.k5.tolerantreader

import eu.k5.tolerantreader.strict.StrictSchema
import javax.xml.stream.XMLStreamWriter


class StrictWriter(private val schema: StrictSchema) {


    fun write(instance: Any, writer: XMLStreamWriter) {

        val context = schema.createContext()

        val type = schema.getElement(instance.javaClass)!!

        type.write(context, instance, writer)


    }


}