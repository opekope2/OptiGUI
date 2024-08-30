package opekope2.optigui.interaction

import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import opekope2.optigui.interaction.data.IInteractionData
import opekope2.optigui.interaction.data.InteractionPlayerData
import opekope2.optigui.screen.IRetexturableScreen
import java.util.function.Supplier

/**
 * Interaction between a player and a container.
 *
 * @param originalTexture The texture to be replaced.
 * @param screen The active GUI screen
 * @param data The details of the interaction
 */
data class Interaction(
    val originalTexture: Identifier,
    val screen: IRetexturableScreen,
    val data: IInteractionData
) {
    /**
     * Details about the interacting player.
     */
    val playerData: InteractionPlayerData
        get() = data.playerData

    /**
     * Extra details about the interaction. May be mutable.
     */
    val extraData: Supplier<NbtCompound>?
        get() = data.extraData
}
