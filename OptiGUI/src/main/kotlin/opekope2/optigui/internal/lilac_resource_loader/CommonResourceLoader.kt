package opekope2.optigui.internal.lilac_resource_loader

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> Collection<T>.assertNotEmpty(): Collection<T> {
    assert(isNotEmpty())
    return this
}

@Suppress("UNCHECKED_CAST")
internal inline fun <TFound, TNotFound> Map<Boolean, List<*>>.split(notFoundAction: (List<TNotFound>) -> Nothing): List<TFound> {
    if (false in this) {
        notFoundAction(this[false] as List<TNotFound>)
    }
    return this.getOrDefault(true, emptyList<TFound>()) as List<TFound>
}

internal fun joinNotFound(strings: List<String>) = strings.joinToString(", ", prefix = "`", postfix = "`")

internal fun wildcardToRegex(wildcard: String): String = opekope2.util.buildString {
    append('^')

    for (char in wildcard) {
        append(
            when (char) {
                '*' -> ".+"
                '?' -> ".*"
                '.' -> "\\."
                '\\' -> "\\\\"
                '+' -> "\\+"
                '^' -> "\\^"
                '$' -> "\\$"
                '[' -> "\\["
                ']' -> "\\]"
                '{' -> "\\{"
                '}' -> "\\}"
                '(' -> "\\("
                ')' -> "\\)"
                '|' -> "\\|"
                '/' -> "\\/"
                else -> char.toString()
            }
        )
    }

    append('$')
}
