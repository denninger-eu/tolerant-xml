package eu.k5.tolerantreader.binding.dom

import eu.k5.tolerantreader.InitContext
import org.junit.jupiter.api.Test
import javax.xml.namespace.QName

class DomWriterTest {

    @Test
    fun test() {

        val domWriter = DomWriter()

        val createSupplier = domWriter.createSupplier(InitContext(), QName("", ""))

        val domValue = createSupplier(QName("", "")) as DomValue
        val domValue2 = createSupplier(QName("", "")) as DomValue

        println(domValue)
        println(domValue2)

    }
}