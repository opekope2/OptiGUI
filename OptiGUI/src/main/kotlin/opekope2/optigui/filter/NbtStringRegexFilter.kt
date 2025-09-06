package opekope2.optigui.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.util.dynamic.Codecs
import java.util.function.Function

/**
 * An NBT filter, which matches an NBT string against a regular expression.
 *
 * @param regex The regular expression to match NBT with
 */
class NbtStringRegexFilter(val regex: Regex) : INbtFilter {
    override fun test(nbt: NbtElement) =
        if (nbt !is NbtString) false
        else regex.matches(nbt.asString())

    companion object {
        /**
         * A case-sensitive regular expression codec for [NbtStringRegexFilter].
         */
        @JvmField
        val CASE_SENSITIVE_REGEX_CODEC: Codec<NbtStringRegexFilter> = createCodec { it.toRegex() }

        /**
         * A case-insensitive regular expression codec for [NbtStringRegexFilter].
         */
        @JvmField
        val CASE_INSENSITIVE_REGEX_CODEC: Codec<NbtStringRegexFilter> = createCodec {
            it.toRegex(RegexOption.IGNORE_CASE)
        }

        /**
         * A case-sensitive wildcard codec for [NbtStringRegexFilter].
         */
        @JvmField
        val CASE_SENSITIVE_WILDCARD_CODEC: Codec<NbtStringRegexFilter> = createCodec { wildcardToRegex(it).toRegex() }

        /**
         * A case-insensitive wildcard codec for [NbtStringRegexFilter].
         */
        @JvmField
        val CASE_INSENSITIVE_WILDCARD_CODEC: Codec<NbtStringRegexFilter> = createCodec {
            wildcardToRegex(it).toRegex(RegexOption.IGNORE_CASE)
        }

        private fun createCodec(toRegex: Function<String, Regex>) = Codecs.exceptionCatching(
            Codec.STRING.xmap(toRegex, Regex::pattern).xmap(::NbtStringRegexFilter, NbtStringRegexFilter::regex)
        )

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
