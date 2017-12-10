package eu.k5.tolerantreader.source.model

import com.example.myschema.StrictRoot
import org.junit.jupiter.api.Test
import java.util.*

class XjcRegistryTest {

    @Test
    fun test() {

        val registry = XjcRegistry(Arrays.asList(StrictRoot::class.java))

        registry.init()
    }
}