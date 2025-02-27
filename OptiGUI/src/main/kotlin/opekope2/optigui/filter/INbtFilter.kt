package opekope2.optigui.filter

import net.minecraft.nbt.NbtElement
import java.util.function.Predicate

/**
 * Functional interface for filtering [NbtElement]s.
 */
fun interface INbtFilter : Predicate<NbtElement>
