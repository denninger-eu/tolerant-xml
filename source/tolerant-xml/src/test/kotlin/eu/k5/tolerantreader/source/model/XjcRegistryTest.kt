package eu.k5.tolerantreader.source.model

import eu.k5.tr.model.Shoe
import eu.k5.tr.model.ShoesizeSimpleContent
import eu.k5.tr.strict.StrictRoot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import javax.xml.namespace.QName

class XjcRegistryTest {

    @Test
    fun resolveRootType() {

        val type = StrictRoot::class.java;

        val registry = XjcRegistry(Arrays.asList(type))

        registry.init()

        val element = registry.getElementByClass(type)
                ?: fail<Nothing>("Strict root not found")

        assertTrue(element.root)
        assertEquals(type, element.type)
        assertEquals("http://k5.eu/tr/strict", element.registry.namespace)
    }

    @Test
    fun resolveComplexType() {

        val type = Shoe::class.java;

        val registry = XjcRegistry(Arrays.asList(type))

        registry.init()

        val xjcType = registry.getElementByClass(ShoesizeSimpleContent::class.java)
                ?: fail<Nothing>("element not found")

        assertEquals(QName("http://k5.eu/tr/model", "shoesizeSimpleContent"), xjcType.qName)
    }


}