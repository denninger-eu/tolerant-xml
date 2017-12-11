package eu.k5.tolerantreader

import eu.k5.tolerantreader.binding.model.Binder
import eu.k5.tolerantreader.binding.model.PackageMapping
import eu.k5.tolerantreader.binding.model.PackageMappingBuilder
import eu.k5.tr.model.NumericTypes
import model.complex.ComplexRoot
import model.complex.SubType
import model.minimal.Root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TolerantReaderBinderTest : AbstractTolerantReaderTest() {

    @Test
    @DisplayName("Read minimal xml")
    fun readMinimal() {
        val obj = readMinimalType("minimal")
                as? Root ?: fail<Nothing>("Invalid root type")

        assertEquals("Hallo", obj.test)
    }


    @Test
    @DisplayName("Read minimal xml, two elements")
    fun readMinimalTwoElements() {
        val obj = readMinimalType("minimal-two-elements")
                as? Root ?: fail<Nothing>("Invalid root type")

        assertEquals("Hallo", obj.test)
        assertEquals("World", obj.test2)
    }


    @Test
    @DisplayName("Read minimal xml, two elements")
    fun readMinimalList() {
        val obj = readMinimalType("minimal-with-list-elements")
                as? Root ?: fail<Nothing>("Invalid root type")

        assertEquals(2, obj.list.size)
        assertEquals("entry1", obj.list[0])
        assertEquals("entry2", obj.list[1])
    }


    @Test
    @DisplayName("Read minimal xml, with attribute")
    fun readMinimalWithAttribute() {
        val obj = readMinimalType("minimal-attributes")
                as? Root ?: fail<Nothing>("Invalid root type")
        assertEquals("Hello", obj.attribute)
    }


    @Test
    @DisplayName("Read minimal xml, with attribute")
    fun readSimpleTypes() {
        val obj = readSimpleType("simple-types")
                as? Root ?: fail<Nothing>("Invalid root type")
        assertEquals("Hello", obj.custom)
    }


    @Test
    @DisplayName("Read complex type")
    fun readComplexTypes() {
        val obj = readComplexType("complex-types")
                as? ComplexRoot ?: fail<Nothing>("Invalid root type")
        assertEquals("dragons", obj.here?.be)
    }

    @Test
    @DisplayName("Read complex type. Cyclic")
    fun readComplexTypesCyclic() {
        val obj = readComplexType("complex-types-cyclic")
                as? ComplexRoot ?: fail<Nothing>("Invalid root type")

        assertEquals("dragons", obj.here?.cyclic?.be)
    }


    @Test
    @DisplayName("Read complex type. Inheritance")
    fun readComplexTypesInheritance() {
        val obj = readComplexType("complex-types-inherited")
                as? ComplexRoot ?: fail<Nothing>("Invalid root type")

        val inherited = obj.inherited as SubType
        assertEquals("baseAttrValue", inherited.baseAttribute)
        assertEquals("subAttrValue", inherited.subAttribute)
        assertEquals("baseValue", inherited.baseElement)
        assertEquals("subValue", inherited.subElement)
    }


    @Test
    @DisplayName("Read simple type. Numeric Types")
    fun readSimpleTypeNumeric() {
        val obj = readModelType("simple-type-numeric")
                as? NumericTypes ?: fail<Nothing>("Invalid root type")

        assertEquals(1, obj.byte)

    }


    override fun getReader(path: String): TolerantReader = reader.getReader(path)

    companion object {
        val reader = ReaderCache(Binder(packageMapping()))

        private fun packageMapping(): PackageMapping {
            val builder = PackageMappingBuilder()
            builder.add("http://k5.eu/tr/minimal", "model.minimal")
            builder.add("http://k5.eu/tr/complex", "model.complex")

            builder.add("http://k5.eu/tr/model", NumericTypes::class.java.`package`.name)
            return builder.build()
        }
    }
}

