package eu.k5.tolerantreader.binding

import eu.k5.tolerantreader.BindContext

interface Assigner {

    fun assign(context: BindContext, instance: Any, value: Any?)

}

object NoopAssigner : Assigner {
    override fun assign(bindContext: BindContext, instance: Any, value: Any?) {
    }
}