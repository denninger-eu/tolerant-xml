package eu.k5.tolerantreader.source.model

<<<<<<< HEAD
import eu.k5.tr.model.idref.Reference
import eu.k5.tr.strict.StrictRoot
import junit.framework.Assert.assertEquals
=======
import eu.k5.tr.model.Shoe
import eu.k5.tr.model.ShoesizeSimpleContent
import eu.k5.tr.strict.StrictRoot
import org.junit.jupiter.api.Assertions.*
>>>>>>> a56fd5910afbe35e9f8b113fdd3907f9d87a064b
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


<<<<<<< HEAD
    @Test
    fun test2() {
        val xjcRegistry = XjcRegistry(Arrays.asList(Reference::class.java))
        xjcRegistry.init()

        val elements = xjcRegistry.getTypes()

        assertEquals(2, elements.size)
    }
=======
>>>>>>> a56fd5910afbe35e9f8b113fdd3907f9d87a064b
}