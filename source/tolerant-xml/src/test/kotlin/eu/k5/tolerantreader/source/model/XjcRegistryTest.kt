package eu.k5.tolerantreader.source.model

import eu.k5.tr.strict.StrictRoot
import org.junit.jupiter.api.Test
import java.util.*

class XjcRegistryTest {

    @Test
    fun test() {

        val registry = XjcRegistry(Arrays.asList(StrictRoot::class.java))

        registry.init()
    }
}