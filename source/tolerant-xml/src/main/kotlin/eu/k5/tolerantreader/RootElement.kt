package eu.k5.tolerantreader

interface RootElement {

    fun seal(context: BindContext): Any?

}