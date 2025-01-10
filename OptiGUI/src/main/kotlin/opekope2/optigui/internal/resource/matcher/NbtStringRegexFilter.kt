package opekope2.optigui.internal.resource.matcher

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import opekope2.optigui.filter.INbtFilter

internal class NbtStringRegexFilter(private val regex: Regex) : INbtFilter {
    override fun test(nbt: NbtElement) =
        if (nbt is NbtString) regex.matches(nbt.asString())
        else false

    companion object {
        val REGEX_DECODER: Decoder<INbtFilter> =
            Codec.STRING.flatMap { createRegex(wildcardToRegex(it), false) }.map(::NbtStringRegexFilter)
        val REGEX_IGNORE_CASE_DECODER: Decoder<INbtFilter> =
            Codec.STRING.flatMap { createRegex(wildcardToRegex(it), true) }.map(::NbtStringRegexFilter)
        val WILDCARD_DECODER: Decoder<INbtFilter> =
            Codec.STRING.flatMap { createRegex(it, false) }.map(::NbtStringRegexFilter)
        val WILDCARD_IGNORE_CASE_DECODER: Decoder<INbtFilter> =
            Codec.STRING.flatMap { createRegex(it, true) }.map(::NbtStringRegexFilter)

        private fun createRegex(regex: String, ignoreCase: Boolean) = try {
            val regexOptions = if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else setOf()
            DataResult.success(Regex(regex, regexOptions))
        } catch (e: Exception) {
            DataResult.error { e.message }
        }

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
