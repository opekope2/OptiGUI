package opekope2.optigui.internal

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.interaction.canReplaceTexture

internal object InteractionHandler {
    internal var filter: Filter<Interaction, Identifier> = object : Filter<Interaction, Identifier>() {
        override fun test(value: Interaction) = FilterResult<Identifier>(skip = true)
    }

    private var currentScreen: HandledScreen<*>? = null

    private var interactionData: Any? = null

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        // Don't bother replacing GUI texture if it's not open
        val screen = currentScreen ?: return texture

        if (!canReplaceTexture(texture)) return texture

        return filter.test(Interaction(texture, screen.title, interactionData))
            .let { if (!it.skip && it.match) it.result else null } ?: texture
    }

}
