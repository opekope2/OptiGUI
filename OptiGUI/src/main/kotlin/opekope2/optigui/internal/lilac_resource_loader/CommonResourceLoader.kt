package opekope2.optigui.internal.lilac_resource_loader

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> Collection<T>.assertNotEmpty(): Collection<T> {
    assert(isNotEmpty())
    return this
}

internal inline fun <T, TResult> Collection<T>.map(
    transform: (T) -> TResult?,
    whenNotTransformed: (Collection<T>) -> Nothing
): Collection<TResult> {
    val result = mutableListOf<TResult>()
    val notTransformed = mutableListOf<T>()

    for (item in this) {
        val transformed = transform(item)
        if (transformed != null) result += transformed
        else notTransformed += item
    }

    if (notTransformed.isNotEmpty()) {
        whenNotTransformed(notTransformed)
    }

    return result
}

internal fun joinNotFound(strings: Collection<String>) = strings.joinToString(", ", prefix = "`", postfix = "`")

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
