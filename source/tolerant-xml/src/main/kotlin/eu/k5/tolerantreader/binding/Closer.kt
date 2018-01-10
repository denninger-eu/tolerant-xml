package eu.k5.tolerantreader.binding

import eu.k5.tolerantreader.BindContext

interface Closer {

    fun close(bindContext: BindContext, instance: Any)


}