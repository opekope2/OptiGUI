package opekope2.util

import opekope2.filter.Filter
import java.util.*

class TreeFormatter {
    private val builder = StringBuilder()
    private val styles = Stack<Boolean>()

    fun indent() {
        styles.push(true)
    }

    fun indent(function: TreeFormatter.() -> Unit) {
        indent()
        function()
        unindent()
    }

    fun unindent() {
        styles.pop()
    }

    @JvmOverloads
    fun append(line: String, lastChild: Boolean = false) {
        if (lastChild) {
            styles.pop()
            styles.push(false)
        }

        for (i in 0 until (styles.count() - 1)) {
            builder.append(if (styles[i]) "│ " else "  ")
        }

        builder.append(if (lastChild) "└─" else "├─")
        builder.appendLine(line)
    }

    override fun toString() = builder.toString()
}

fun Filter<*, *>.dump() = dump(TreeFormatter(), last = true)

private fun Filter<*, *>.dump(writer: TreeFormatter, last: Boolean): String {
    writer.indent()
    writer.append(toString(), lastChild = last)

    if (this is Iterable<*>) {
        val it = iterator()
        while (it.hasNext()) {
            val next = it.next()
            if (next is Filter<*, *>) next.dump(writer, !it.hasNext())
            else writer.indent { append(next.toString(), lastChild = !it.hasNext()) }
        }
    }

    writer.unindent()

    return writer.toString()
}

