@file: JvmName("WildcardRegexParser")

package opekope2.util

import org.apache.commons.text.StringEscapeUtils.unescapeJava

/**
 * Parses a wildcard or regex according to the [OptiGUI syntax](https://opekope2.github.io/OptiGUI-Next/syntax/#strings).
 */
fun parseWildcardOrRegex(wildcardOrRegex: String): Regex = when {
    wildcardOrRegex.startsWith("pattern:") -> Regex(
        wildcardToRegex(unescapeJava(wildcardOrRegex.substring("pattern:".length)))
    )

    wildcardOrRegex.startsWith("ipattern:") -> Regex(
        wildcardToRegex(unescapeJava(wildcardOrRegex.substring("ipattern:".length))),
        RegexOption.IGNORE_CASE
    )

    wildcardOrRegex.startsWith("regex:") -> Regex(
        unescapeJava(wildcardOrRegex.substring("regex:".length))
    )

    wildcardOrRegex.startsWith("iregex:") -> Regex(
        unescapeJava(wildcardOrRegex.substring("iregex:".length)),
        RegexOption.IGNORE_CASE
    )

    else -> Regex(wildcardOrRegex, RegexOption.LITERAL)
}

private fun wildcardToRegex(wildcard: String): String {
    val result = StringBuilder("^")

    for (char in wildcard) {
        result.append(
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
                else -> char
            }
        )
    }

    result.append('$')
    return result.toString()
}
