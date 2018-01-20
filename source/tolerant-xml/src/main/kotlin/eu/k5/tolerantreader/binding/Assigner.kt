package eu.k5.tolerantreader.binding

import eu.k5.tolerantreader.ReaderContext

interface Assigner {

    fun assign(context: ReaderContext, instance: Any, value: Any?)

}

