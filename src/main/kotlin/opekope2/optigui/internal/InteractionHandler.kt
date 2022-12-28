package opekope2.optigui.internal

import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.optigui.Interaction

internal object InteractionHandler {
    internal var filter: Filter<Interaction, Identifier> = object : Filter<Interaction, Identifier>() {
        override fun test(value: Interaction) = FilterResult<Identifier>(skip = true)
    }

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier =
        filter.test(Interaction(texture, null)).let { if (!it.skip && it.match) it.replacement ?: texture else texture }
}
