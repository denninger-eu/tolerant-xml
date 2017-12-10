package eu.k5.tolerantreader.tolerant

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import javax.xml.namespace.QName

class TolerantMapTest {

    private var builder: TolerantMapBuilder<String>? = null
    @BeforeEach
    fun setUp() {
        builder = TolerantMapBuilder()
    }

    @DisplayName("Add one element, can be retrieved by exact name")
    @Test
    fun retrievedByExactName() {
        val name = QName("x", "a")
        builder?.append(name, "a")

        Assertions.assertEquals("a", builder?.build()?.get(name))
    }


    @DisplayName("Add two elements, different namespace, retrieve by exact name")
    @Test
    fun sameLocalName__retrieveByEactName() {

        val nameA1 = QName("x", "a")
        val nameA2 = QName("y", "a")
        builder?.append(nameA1, "a1")
        builder?.append(nameA2, "a2")

        val map = builder!!.build()
        Assertions.assertEquals("a1", map.get(nameA1))
        Assertions.assertEquals("a2", map.get(nameA2))
    }

    @DisplayName("Add three elements, two are equal for each char, retrieve by prefix name")
    @Test
    fun sameLocalNamePrefix__retrieveByPrefixEactName() {

        builder?.append(QName("xxy", "a"), "a1")
        builder?.append(QName("xyx", "a"), "a2")
        builder?.append(QName("yxx", "a"), "a3")

        val map = builder!!.build()
        Assertions.assertEquals("a1", map.get("xx", "a"))
        Assertions.assertEquals("a2", map.get("xy", "a"))
        Assertions.assertEquals("a3", map.get("yx", "a"))
    }


    @DisplayName("Add two elements, namespace difference in length, retrieve by prefix name")
    @Test
    fun sameLocalNamespaceDifferentByLength__retrieveByPrefixEactName() {

        builder?.append(QName("x", "a"), "a1")
        builder?.append(QName("xx", "a"), "a2")

        val map = builder!!.build()
        Assertions.assertEquals("a1", map.get("x", "a"))
        Assertions.assertEquals("a2", map.get("xx", "a"))
    }

}