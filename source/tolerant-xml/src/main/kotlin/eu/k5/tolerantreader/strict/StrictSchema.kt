package eu.k5.tolerantreader.strict

import com.google.common.collect.ImmutableMap

class StrictSchema(private val elements: ImmutableMap<Class<*>, StrictElement>,
                   private val types: ImmutableMap<Class<*>, StrictComplexType>,
                   val namespaces: ImmutableMap<String, String>) {

    fun getElement(type: Class<*>): StrictElement?
            = elements[type]


    fun getType(type: Class<*>): StrictType?
            = types[type]


    fun createContext(): StrictContext
            = StrictContext(this)


}