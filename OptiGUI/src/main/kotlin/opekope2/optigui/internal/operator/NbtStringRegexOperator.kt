package opekope2.optigui.internal.operator

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.nbt.NbtString
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.operator.INbtOperator
import opekope2.optigui.util.mapMessage
import opekope2.optigui.util.unwrap

internal open class NbtStringRegexOperator(private val ignoreCase: Boolean) : INbtOperator {
    override fun <T> createFilter(ops: DynamicOps<T>, input: T): DataResult<INbtFilter> {
        val string = ops.getStringValue(input).unwrap { return it.mapMessage() }
        val regex = createRegex(string).unwrap { return it.mapMessage() }
        val filter = INbtFilter { nbt ->
            if (nbt is NbtString) regex.matches(nbt.asString())
            else false
        }
        return DataResult.success(filter)
    }

    protected open fun createRegex(regex: String): DataResult<Regex> = try {
        val regexOptions = if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else setOf()
        DataResult.success(Regex(regex, regexOptions))
    } catch (e: Exception) {
        DataResult.error { e.message }
    }

    private class Wildcard(ignoreCase: Boolean) : NbtStringRegexOperator(ignoreCase) {
        override fun createRegex(regex: String): DataResult<Regex> {
            return super.createRegex(wildcardToRegex(regex))
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

    companion object {
        @JvmField
        val REGEX = NbtStringRegexOperator(false)

        @JvmField
        val REGEX_IGNORE_CASE = NbtStringRegexOperator(true)

        @JvmField
        val WILDCARD: NbtStringRegexOperator = Wildcard(false)

        @JvmField
        val WILDCARD_IGNORE_CASE: NbtStringRegexOperator = Wildcard(true)
    }
}
