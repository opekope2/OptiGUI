package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
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
    fun get(interaction: IInteraction, lookup: HolderLookup.Provider): Tag?

    /**
     * Interaction NBT provider registry.
     *
     * The interaction NBT is an [CompoundTag], where the keys are the keys registered in [Registry], and the values are
     * obtained using [get].
     */
    companion object Registry : RegistryBase<String, IInteractionNbtProvider>()
}
