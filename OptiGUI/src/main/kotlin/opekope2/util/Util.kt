package opekope2.util

import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.screen.*
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import opekope2.filter.FilterResult
import java.io.StringWriter

/**
 * Converts the given string to a boolean:
 * - `true`, if the string is "true" (case-insensitive)
 * - `false`, if the string is "false" (case-insensitive)
 * - `null` otherwise
 */
internal fun String?.toBoolean(): Boolean? {
    return (this ?: return null).lowercase().toBooleanStrictOrNull()
}

/**
 * If the current [FilterResult] is [FilterResult.Match], change its result to the given one.
 * Otherwise, return the original.
 *
 * @param TOld The type of the old result
 * @param TNew The type of the new result
 * @param result The new result
 */
fun <TOld, TNew> FilterResult<TOld>.withResult(result: TNew): FilterResult<TNew> = when (this) {
    is FilterResult.Skip -> FilterResult.Skip()
    is FilterResult.Mismatch -> FilterResult.Mismatch()
    is FilterResult.Match -> FilterResult.Match(result)
}

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
    this.split(*delimiters).filter { it.isNotEmpty() }

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
fun ScreenHandler.getComparatorOutputWorkaround(): Int = when (this) {
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
