package eu.k5.tolerantreader.strict

interface StrictTypeAdapter {

    fun convert(any: Any): String

}

object ToStringAdapter : StrictTypeAdapter {

    override fun convert(any: Any): String = any.toString()


}