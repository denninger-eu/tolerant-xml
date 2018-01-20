package eu.k5.tolerantreader.binding

import eu.k5.tolerantreader.ReaderContext

interface Closer {

    fun close(bindContext: ReaderContext, instance: Any)


}