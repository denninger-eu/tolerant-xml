package eu.k5.tolerantreader.binding.model

class ReflectionUtils {

    fun sanitizeAsClassName(name: String): String {
        if (name.isEmpty()) {
            return ""
        }
        if (name.length == 1) {
            return name[0].toUpperCase().toString()
        }
        if (name.contains('_')) {
            val nameparts = name.split('_')
            val nameBuilder = StringBuilder()

            nameparts
                    .filter { it.isNotEmpty() }
                    .forEach { nameBuilder.append(it[0].toUpperCase()).append(it.substring(1)) }

            return nameBuilder.toString()
        }
        return name[0].toUpperCase() + name.substring(1)
    }

    fun getSetterName(propertyName: String): String {
        return "set" + propertyName.get(0).toUpperCase() + propertyName.substring(1)
    }

    fun getGetterName(propertyName: String): String {
        return "get" + propertyName.get(0).toUpperCase() + propertyName.substring(1)
    }

}