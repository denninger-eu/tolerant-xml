package eu.k5.tolerant.converter

import com.google.common.collect.Sets
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AnalysePrefixTest {

    @Test
    fun declaredAtTopLevel() {
        val prefixes = AnalysePrefixes.ana("""<abc xmlns:xx="http://" ></abc>""")
        assertEquals(Sets.newHashSet("xx"), prefixes.declared)
        assertEquals(Sets.newHashSet<String>(), prefixes.used)
        assertEquals(Sets.newHashSet<String>(), prefixes.missing)
    }

    @Test
    fun usedAndDeclardAtTopLevel() {
        val prefixes = AnalysePrefixes.ana("""<xx:abc xmlns:xx="http://" ></xx:abc>""")
        assertEquals(Sets.newHashSet("xx"), prefixes.declared)
        assertEquals(Sets.newHashSet("xx"), prefixes.used)
        assertEquals(Sets.newHashSet<String>(), prefixes.missing)
    }

    @Test
    fun usedAtTopLevel() {
        val prefixes = AnalysePrefixes.ana("""<xx:abc ></xx:abc>""")
        assertEquals(Sets.newHashSet<String>(), prefixes.declared)
        assertEquals(Sets.newHashSet("xx"), prefixes.used)
        assertEquals(Sets.newHashSet("xx"), prefixes.missing)
    }


    @Test
    fun declaredChildUsedAtTopLevel() {
        val prefixes = AnalysePrefixes.ana("""<xx:abc ><b xmlns:xx="http://"></b></xx:abc>""")
        assertEquals(Sets.newHashSet("xx"), prefixes.declared)
        assertEquals(Sets.newHashSet("xx"), prefixes.used)
        assertEquals(Sets.newHashSet("xx"), prefixes.missing)
    }

    @Test
    fun usedAtAttributeName() {
        val prefixes = AnalysePrefixes.ana("""<abc yy:test=""></abc>""")
        assertEquals(Sets.newHashSet<String>(), prefixes.declared)
        assertEquals(Sets.newHashSet("yy"), prefixes.used)
        assertEquals(Sets.newHashSet("yy"), prefixes.missing)
    }


    @Test
    fun declaredTwiceAtChild() {
        val prefixes = AnalysePrefixes.ana("""<abc><b xmlns:xx="http://"><xx:a></xx:a></b>
            <b xmlns:xx="http://"><xx:a></xx:a></b></abc>""".trimMargin())
        assertEquals(Sets.newHashSet("xx"), prefixes.declared)
        assertEquals(Sets.newHashSet("xx"), prefixes.used)
        assertEquals(Sets.newHashSet<String>(), prefixes.missing)
    }
}