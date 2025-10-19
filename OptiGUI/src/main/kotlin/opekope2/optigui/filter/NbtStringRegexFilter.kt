package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.util.dynamic.Codecs
import java.util.function.UnaryOperator
import java.util.regex.PatternSyntaxException

/**
 * An NBT filter, which matches an NBT string against a regular expression.
 *
 * @param pattern The wildcard or regular expression (determined by [type]) to match NBT with
 * @param type The type describing this filter
 * @throws PatternSyntaxException If [pattern] is not a valid wildcard or regex
 */
class NbtStringRegexFilter(val pattern: String, override val type: Type) : INbtFilter {
    private val regex: Regex = type.toRegex.apply(pattern).toRegex(type.regexOptions)

    override fun test(nbt: NbtElement, root: NbtElement) =
        if (nbt !is NbtString) false
        else regex.matches(nbt.asString())

    override fun toString() = super.asString() + " " + pattern

    /**
     * A type describing an [NbtStringRegexFilter].
     *
     * @param toRegex A function which converts the input to a regular expression
     * @param regexOptions Flags which control how a regex matches a string
     */
    enum class Type(val toRegex: UnaryOperator<String>, val regexOptions: Set<RegexOption>) :
        INbtFilter.IType<NbtStringRegexFilter> {
        /**
         * An [NbtStringRegexFilter] type, which represents a case-sensitive regex.
         */
        CASE_SENSITIVE_REGEX(UnaryOperator.identity(), emptySet()),

        /**
         * An [NbtStringRegexFilter] type, which represents a case-insensitive regex.
         */
        CASE_INSENSITIVE_REGEX(UnaryOperator.identity(), setOf(RegexOption.IGNORE_CASE)),

        /**
         * An [NbtStringRegexFilter] type, which represents a case-sensitive wildcard.
         */
        CASE_SENSITIVE_WILDCARD(::wildcardToRegex, emptySet()),

        /**
         * An [NbtStringRegexFilter] type, which represents a case-insensitive wildcard.
         */
        CASE_INSENSITIVE_WILDCARD(::wildcardToRegex, setOf(RegexOption.IGNORE_CASE));

        override val codec: Codec<NbtStringRegexFilter> = Codecs.exceptionCatching(
            Codec.STRING.xmap({ NbtStringRegexFilter(it, this) }, NbtStringRegexFilter::pattern)
        )

        private companion object {
            private fun wildcardToRegex(wildcard: String) = buildString {
                append('^')

                for (char in wildcard) {
                    append(
                        when (char) {
                            '*' -> ".*"
                            '?' -> "."
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
        }
    }
}
