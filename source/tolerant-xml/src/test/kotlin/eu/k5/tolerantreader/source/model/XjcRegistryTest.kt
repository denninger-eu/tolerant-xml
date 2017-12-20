package eu.k5.tolerantreader.source.model

import eu.k5.tr.model.idref.Reference
import eu.k5.tr.strict.StrictRoot
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class XjcRegistryTest {

    @Test
    fun test() {

        val registry = XjcRegistry(Arrays.asList(StrictRoot::class.java))

        registry.init()
    }


    @Test
    fun test2() {
        val xjcRegistry = XjcRegistry(Arrays.asList(Reference::class.java))
        xjcRegistry.init()

        val elements = xjcRegistry.getTypes()

        assertEquals(2, elements.size)
    }
}