package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import opekope2.optigui.interaction.IInteraction
import java.util.function.Function

/**
 * Provides the NBT of an entity.
 *
 * @param entityGetter A function that gets the [Entity] from the interaction, if applicable.
 */
class EntityNbtProvider(private val entityGetter: Function<IInteraction, Entity?>) : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, registryAccess: RegistryAccess) =
        entityGetter.apply(interaction)?.saveWithoutId(CompoundTag())
}
