package opekope2.optigui.interaction.data

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import opekope2.optigui.util.INbtConvertible
import opekope2.optigui.util.encode
import java.util.function.Supplier

/**
 * Details about an interaction.
 */
sealed interface IInteractionData : INbtConvertible {
    /**
     * The identifier of the interacted container.
     */
    val id: Identifier

    /**
     * The interaction position.
     */
    val blockPos: BlockPos

    /**
     * The item the player interacted with.
     */
    val item: ItemStack

    /**
     * Details about the interacting player.
     */
    val playerData: InteractionPlayerData

    /**
     * Extra details about the interaction. May be mutable.
     */
    val extraData: Supplier<NbtCompound>?

    /**
     * The world the interaction happened in.
     */
    val world: World
        get() = playerData.player.entityWorld

    override fun writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        compound.encode("pos", blockPos, BlockPos.CODEC, lookup)
        compound.put("item", item.encodeAllowEmpty(lookup))
        playerData.writeNbt(compound, lookup)
        extraData?.get()?.let { compound.put("extra", it) }
        // TODO world
    }
}
