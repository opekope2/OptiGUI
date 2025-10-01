package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.registry.RegistryBase

/**
 * Extracts parts of an interaction as NBT for filtering.
 */
fun interface IInteractionNbtProvider {
    /**
     * Creates NBT from an interaction.
     *
     * @param interaction The interaction to extract NBT from
     * @param lookup The registries of the world. Used to encode objects
     */
    fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtElement?

    /**
     * Interaction NBT provider registry.
     *
     * The interaction NBT is an [NbtCompound], where the keys are the keys registered in [Registry], and the values are
     * obtained using [get].
     */
    companion object Registry : RegistryBase<String, IInteractionNbtProvider>()
}
