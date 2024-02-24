package opekope2.util

import java.util.*

/**
 * An indentation and tree formatter utility class.
 */
class TreeFormatter {
    private val builder = StringBuilder()
    private val styles = Stack<Boolean>()

    /**
     * Indents the following lines by 1.
     */
    fun indent() {
        styles.push(true)
    }

    /**
     * Indents all code ran inside [function] by 1, then resets the indentation to the previous one.
     */
    fun indent(function: TreeFormatter.() -> Unit) {
        indent()
        function()
        unindent()
    }

    /**
     * Unindents the following lines by 1.
     */
    fun unindent() {
        styles.pop()
    }

    /**
     * Appends a line to the tree with indentation.
     *
     * @param line The string to append
     * @param lastChild Whether [line] is the last child of the parent node (for different formatting)
     */
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
