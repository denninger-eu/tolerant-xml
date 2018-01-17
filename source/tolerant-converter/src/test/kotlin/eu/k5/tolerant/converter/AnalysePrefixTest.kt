package eu.k5.tolerant.converter

import com.google.common.collect.Sets
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AnalysePrefixTest {

    @Test
    fun test() {
        val prefixes = AnalysePrefixes.ana("""<abc xmlns:xx="http://" ><ab xmlns:xx="http://"></ab></abc>""")
        assertEquals(Sets.newHashSet("xx"), prefixes.declared)
    }
}