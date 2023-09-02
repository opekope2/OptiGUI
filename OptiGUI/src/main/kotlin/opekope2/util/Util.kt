package opekope2.util

import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.screen.*
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import java.io.StringWriter

/**
 * Trim parentheses from the start and end of a string.
 */
fun String.trimParentheses() = trimStart('(').trimEnd(')')

/**
 * Default delimiters of lists in OptiGUI and OptiFine resources.
 *
 * @see splitIgnoreEmpty
 */
val delimiters = charArrayOf(' ', '\t')

/**
 * Splits a string at the given delimiters and returns every substring, which is not empty.
 */
fun CharSequence.splitIgnoreEmpty(vararg delimiters: Char) =
    this.split(*delimiters).filter { it.isNotEmpty() }.ifEmpty { null }

/**
 * [Identifier] deconstruction helper, which returns the namespace.
 *
 * @see Identifier.namespace
 */
operator fun Identifier.component1(): String = namespace

/**
 * [Identifier] deconstruction helper, which returns the path.
 *
 * @see Identifier.path
 */
operator fun Identifier.component2(): String = path

/**
 * Same as [kotlin.text.buildString], but with [StringWriter].
 */
inline fun buildString(builderAction: StringWriter.() -> Unit): String {
    return StringWriter().apply(builderAction).toString()
}

/**
 * Checks if the receiver of the function is a superclass or superinterface of [obj].
 */
fun Class<*>.isSuperOf(obj: Any) = isAssignableFrom(obj.javaClass)

/**
 * Computes the comparator output based on the screen's inventory.
 */
val ScreenHandler.comparatorOutputWorkaround: Int
    get() = when (this) {
        is BrewingStandScreenHandler -> ScreenHandler.calculateComparatorOutput(inventory)
        is AbstractFurnaceScreenHandler -> ScreenHandler.calculateComparatorOutput(inventory)
        is GenericContainerScreenHandler -> ScreenHandler.calculateComparatorOutput(inventory)
        is Generic3x3ContainerScreenHandler -> ScreenHandler.calculateComparatorOutput(inventory)
        is HopperScreenHandler -> ScreenHandler.calculateComparatorOutput(inventory)
        is ShulkerBoxScreenHandler -> ScreenHandler.calculateComparatorOutput(inventory)
        else -> 0
    }

/**
 * Computes the comparator output of a lectern based on its screen.
 */
val LecternScreen.comparatorOutputWorkaround: Int
    get() {
        val f = if (this.pageCount > 1) this.pageIndex.toFloat() / (this.pageCount.toFloat() - 1.0f) else 1.0f
        return MathHelper.floor(f * 14.0f) + 1
    }

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

internal fun wildcardToRegex(wildcard: String): String = buildString {
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
