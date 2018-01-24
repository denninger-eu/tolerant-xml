package eu.k5.tolerantreader.binding

import eu.k5.tolerantreader.ReaderContext

interface Retriever {

    fun retrieve(context: ReaderContext, instance: Any): Any?

}