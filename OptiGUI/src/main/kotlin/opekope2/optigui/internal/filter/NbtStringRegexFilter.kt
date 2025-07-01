package opekope2.optigui.internal.filter

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import opekope2.optigui.filter.INbtFilter
import com.mojang.serialization.Decoder as DfuDecoder
import kotlin.text.Regex as KRegex

internal class NbtStringRegexFilter(private val regex: KRegex) : INbtFilter {
    override fun test(nbt: NbtElement) =
        if (nbt is NbtString) regex.matches(nbt.asString())
        else false

    sealed class Decoder : DfuDecoder<NbtStringRegexFilter> {
        protected abstract val ignoreCase: Boolean

        override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<NbtStringRegexFilter, T>> =
            ops.getStringValue(input).flatMap(::createRegex).map(::NbtStringRegexFilter)
                .map { Pair.of(it, ops.empty()) }

        protected open fun createRegex(regex: String): DataResult<KRegex> = try {
            val regexOptions = if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else setOf()
            DataResult.success(regex.toRegex(regexOptions))
        } catch (e: Exception) {
            DataResult.error { e.message }
        }

        class Regex(override val ignoreCase: Boolean) : Decoder()

        class Wildcard(override val ignoreCase: Boolean) : Decoder() {
            override fun createRegex(regex: String) = super.createRegex(wildcardToRegex(regex))

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
