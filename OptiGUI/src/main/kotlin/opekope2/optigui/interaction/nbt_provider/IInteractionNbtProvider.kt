package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.RegistryAccess
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
     * @param registryAccess The registries of the world
     */
    fun get(interaction: IInteraction, registryAccess: RegistryAccess): Tag?

    /**
     * Interaction NBT provider registry.
     *
     * The interaction NBT is an [CompoundTag], where the keys are the keys registered in [Registry], and the values are
     * obtained using [get].
     */
    companion object Registry : RegistryBase<String, IInteractionNbtProvider>()
}
