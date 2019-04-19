package Forest.Enums

fun <T: Enum<T>> Enum<T>.getPrettyName() = name[0] +
        name.slice(1 until name.length).map { it.toLowerCase() }.joinToString("")