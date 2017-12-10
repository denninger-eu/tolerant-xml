package eu.k5.tolerantreader

interface Reader {

    fun read(instance: Any): Any?
}