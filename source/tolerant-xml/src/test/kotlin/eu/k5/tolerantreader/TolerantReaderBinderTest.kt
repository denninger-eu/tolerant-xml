package eu.k5.tolerantreader

import eu.k5.tolerantreader.reader.TolerantReader
import eu.k5.tr.model.*
import eu.k5.tr.model.idref.Reference
import eu.k5.tr.model.idref.Referenced
import eu.k5.tr.model.inheritance.ComplexInheritance
import eu.k5.tr.model.inheritance.SubType
import eu.k5.tr.model.inheritance.sub.NsComplexInheritance
import eu.k5.tr.model.inheritance.sub.NsSubType
import model.complex.ComplexRoot
import model.minimal.Root
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.StandardCharsets

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
    @DisplayName("Read minimal xml, with unknown content")
    fun readMinimalUnknownContent() {
        val obj = readMinimalType("minimal-unknown-content")
                as? Root ?: fail<Nothing>("Invalid root type")
        assertEquals("Hallo", obj.test)
    }


    @Test
    @DisplayName("Read minimal xml, with unknown content")
    fun readMinimalComplexContentInSimpleType() {
        val obj = readMinimalType("minimal-complex-content-in-simple")
                as? Root ?: fail<Nothing>("Invalid root type")
        assertEquals("after invalid simpletype", obj.test2)
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
    @DisplayName("Read model type. Inheritance")
    fun readComplexTypesInheritance() {
        val obj = readModelType("complex-type-inheritance")
                as? ComplexInheritance ?: fail<Nothing>("Invalid root type")

        val inherited = obj.type as SubType
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

        assertEquals(1, obj.byte, "byte")
        assertEquals(1, obj.int, "int")
        assertEquals(BigInteger.valueOf(1), obj.integer, "integer")
        assertEquals(1, obj.long, "long")
        assertEquals(BigInteger.valueOf(-1), obj.negativeInteger, "negativeInteger")

        assertEquals(BigInteger.valueOf(0), obj.nonNegativeInteger, "nonNegativeInteger")
        assertEquals(BigInteger.valueOf(0), obj.nonPositiveInteger, "nonPositiveInteger")
        assertEquals(BigInteger.valueOf(1), obj.positiveInteger, "positiveInteger")
        assertEquals(java.lang.Short(1), obj.short, "short")
        assertEquals(BigInteger.valueOf(1), obj.unsignedLong, "unsignedLong")

        assertEquals(java.lang.Long(1), obj.unsignedInt, "unsignedInt")

        assertEquals(1, obj.unsignedShort, "unsignedShort")

        assertEquals(java.lang.Short(1), obj.unsignedByte, "unsignedShort")

        assertEquals(2.02, obj.double, "double")

        assertEquals(1.01f, obj.float, "float")
    }


    @Test
    @DisplayName("Read model type. Misc Types")
    fun readSimpleTypeMisc() {
        val obj = readModelType("simple-type-misc")
                as? MiscTypes ?: fail<Nothing>("Invalid root type")

        assertEquals("http://www.k5.eu", obj.anyUri)

        assertArrayEquals("test".toByteArray(StandardCharsets.UTF_8), obj.base64Binary)
    }

    @Test
    @Disabled
    @DisplayName("Read simple type. Temporal Types")
    fun readSimpleTypeTemporal() {
        val obj = readModelType("simple-type-temporal")
                as? DateTypes ?: fail<Nothing>("Invalid root type")

        assertEquals(13, obj.date.day)
        assertEquals(12, obj.date.month)
        assertEquals(2017, obj.date.year)
    }

    @Test
    @DisplayName("Read model type. idref")
    fun readModelIdref() {
        val obj = readModelType("complex-type-idref")
                as? Reference ?: fail<Nothing>("Invalid root type")

        assertIdref(obj)
    }


    @Test
    @DisplayName("Read model type. idref forward")
    fun readModelIdrefForward() {
        val obj = readModelType("complex-type-idref-forward")
                as? Reference ?: fail<Nothing>("Invalid root type")

        assertIdref(obj)
    }


    private fun assertIdref(reference: Reference) {
        val idref = reference.idref
        val referenced = reference.referenced


        assertEquals("firstname", referenced.firstname)
        assertEquals("lastname", referenced.lastname)
        if (idref is Referenced) {
            assertEquals("firstname", idref.firstname)
            assertEquals("lastname", idref.lastname)
        } else {
            fail("Invalid type in idref: " + idref)
        }
    }

    @Test
    @DisplayName("Read model type. nillable type")
    fun readModelNillable() {
        val obj = readModelType("simple-type-nillable")
                as? NillableType ?: fail<Nothing>("Invalid root type")

        if (obj.nillable == null) {
            fail<Nothing>("nillable not present")
        }

        assertEquals("present", obj.nillable.value)
    }


    @Test
    @DisplayName("Read model type. list")
    fun readModelList() {
        val obj = readModelType("simple-type-list")
                as? ListType ?: fail<Nothing>("Invalid root type")


        assertEquals(2, obj.list.size)

        assertEquals("abc", obj.list[0])
        assertEquals("edf", obj.list[1])
    }

    @Test
    @DisplayName("Read model type. enum")
    fun readModelEnum() {
        val obj = readModelType("simple-type-enum")
                as? EnumType ?: fail<Nothing>("Invalid root type")



        assertEquals(SizeEnum.XL, obj.size)
        assertEquals(SizeEnum.S, obj.sizeAttribute)
    }

    @Test
    @DisplayName("Read model type. enum invalid")
    fun readModelEnumInvalid() {
        val obj = readModelType("simple-type-enum-invalid")
                as? EnumType ?: fail<Nothing>("Invalid root type")



        assertNull(obj.size)
        assertNull(obj.sizeAttribute)
    }


    @Test
    @DisplayName("Read model type. complex-type simpleontent")
    fun readModelComplextTypeSimpleContent() {
        val obj = readModelType("complex-type-simplecontent")
                as? Shoe ?: fail<Nothing>("Invalid root type")

        assertNotNull(obj.size)
        assertEquals("abc", obj.size.value)
        assertEquals("edf", obj.size.country)
    }


    @Test
    @DisplayName("Read model type. complex-type simpleontent")
    fun readModelRestrictionBases() {
        val obj = readModelType("simple-type-restrictionbases")
                as? BaseType ?: fail<Nothing>("Invalid root type")

        assertEquals("test", obj.myid)
        assertEquals(BigInteger("10"), obj.myinteger)
        assertEquals(BigDecimal("1.0"), obj.mydecimal)
    }


    @Test
    @DisplayName("Read model type. With transformer")
    fun readModelTransformer() {
        val obj = readModelType("transformer")
                as? ComplexInheritance ?: fail<Nothing>("Invalid root type")

        assertEquals("subValue", (obj.type as SubType).subElement)
    }


    @Test
    @DisplayName("Read model type. With transformer for complex type")
    fun readModelTransformerComplex() {
        val obj = readModelType("transformer-complex-type")
                as? FullPerson ?: fail<Nothing>("Invalid root type")

        assertEquals("InfoAttrib", obj.info.attrib)
        assertEquals("FirstName", obj.info.firstname)
    }

    @Test
    @DisplayName("Read model type. With transformer for inherited type")
    fun readModelTransformerInheritance() {
        val obj = readModelType("transformer-inheritance-ns")
                as? NsComplexInheritance ?: fail<Nothing>("Invalid root type")

        val inherited = obj.type as NsSubType
        assertEquals("baseAttrValue", inherited.baseAttribute)
        assertEquals("subAttrValue", inherited.subAttribute)
        assertEquals("subValue", inherited.subElement)
        assertEquals("baseValue", inherited.baseElement)
    }


    @Test
    @DisplayName("Read model type. With transformer for inherited type")
    fun readModelTransformerInheritanceAbstract() {
        val obj = readModelType("transformer-inheritance-abstract")
                as? NsComplexInheritance ?: fail<Nothing>("Invalid root type")

        val inherited = obj.type as NsSubType
        assertEquals("baseAttrValue", inherited.baseAttribute)
        assertEquals("subAttrValue", inherited.subAttribute)
        assertEquals("subValue", inherited.subElement)
        assertEquals("baseValue", inherited.baseElement)

        assertEquals("abstractValue", inherited.abstractElement)
    }



    @Test
    @DisplayName("Read model type. With transformer for inherited type")
    fun readModelTransformerInheritedTransformer() {
        val obj = readModelType("transformer-inherited-transformer")
                as? NsComplexInheritance ?: fail<Nothing>("Invalid root type")

        val inherited = obj.type as NsSubType
        assertEquals("baseAttrValue", inherited.baseAttribute)
        assertEquals("subAttrValue", inherited.subAttribute)
        assertEquals("baseValue", inherited.baseElement)
        assertEquals("subValue", inherited.subElement)
    }

    @Test
    @DisplayName("Read model type. With transformer two step path")
    fun readModelTransformerTargetPath() {
        val obj = readModelType("transformer-two-step-path")
                as? FullPerson ?: fail<Nothing>("Invalid root type")

        assertEquals("InfoAttrib", obj.info.attrib)
        assertEquals("MyName", obj.info.firstname)
        assertEquals("PostString", obj.post)
    }


    @Test
    @DisplayName("Read model type. With transformer two step path")
    fun readModelTransformerIntegrateIntoExisting() {
        val obj = readModelType("transformer-integrate-into-existing")
                as? FullPerson ?: fail<Nothing>("Invalid root type")

        assertEquals("InfoAttrib", obj.info.attrib)
        assertEquals("MyName", obj.info.firstname)
        assertEquals("PostString", obj.post)
    }

    @Test
    @DisplayName("Read model type. Multiple elements for scalar value")
    fun readModelListIntoScalar() {
        val obj = readModelType("complex-type-list-into-scalar")
                as? FullPerson ?: fail<Nothing>("Invalid root type")

        assertEquals("InfoAttrib", obj.info.attrib)
        assertEquals("SecondFirstName", obj.info.firstname)
    }

    @Test
    @DisplayName("Read model type. Multiple elements for scalar value")
    fun readModelComplexTypeListIntoScalar() {
        val obj = readModelType("complex-type-complex-type-list-into-scalar")
                as? FullPerson ?: fail<Nothing>("Invalid root type")

        assertEquals("InfoAttrib2", obj.info.attrib)
        assertEquals("SecondFirstName", obj.info.firstname)
    }


    override fun getReader(path: String): TolerantReader = reader.getReader(path)


}

