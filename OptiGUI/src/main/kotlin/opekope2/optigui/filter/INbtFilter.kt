package opekope2.optigui.filter

import net.minecraft.nbt.NbtElement
import java.util.function.Predicate

/**
 * Functional interface for filtering [NbtElement]s.
 */
fun interface INbtFilter : Predicate<NbtElement> {
    override fun and(other: Predicate<in NbtElement>) = INbtFilter { test(it) && other.test(it) }

    override fun negate() = INbtFilter { !test(it) }

    override fun or(other: Predicate<in NbtElement>) = INbtFilter { test(it) || other.test(it) }
}
