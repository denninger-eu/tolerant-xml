package eu.k5.tolerantreader.strict

import com.google.common.collect.ImmutableMap

class StrictSchema(private val elements: ImmutableMap<Class<*>, StrictElement>) {

    fun get(type: Class<*>): StrictElement {

        return elements.get(type)!! as StrictElement
    }


}