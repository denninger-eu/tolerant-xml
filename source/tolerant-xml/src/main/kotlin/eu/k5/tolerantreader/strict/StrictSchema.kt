package eu.k5.tolerantreader.strict

import com.google.common.collect.ImmutableMap

class StrictSchema(private val elements: ImmutableMap<Class<*>, StrictElement>,
                   private val types: ImmutableMap<Class<*>, StrictComplexType>,
                   val namespaces: ImmutableMap<String, String>) {

    fun get(type: Class<*>): StrictElement {

        return elements.get(type)!! as StrictElement
    }

    fun getType(type: Class<*>): StrictType? {
        return types.get(type)
    }

    fun createContext(): StrictContext {
        return StrictContext(this)

    }


}