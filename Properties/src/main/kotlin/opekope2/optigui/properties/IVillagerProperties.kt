package opekope2.optigui.properties

import net.minecraft.util.Identifier
import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for villagers.
 *
 * @see net.minecraft.entity.passive.VillagerEntity
 * @see opekope2.optigui.properties.impl.VillagerProperties
 */
interface IVillagerProperties : IInteractionData {
    /**
     * The profession ID of the villager.
     *
     * @see net.minecraft.village.VillagerData.getProfession
     * @see net.minecraft.village.VillagerProfession
     */
    val profession: Identifier

    /**
     * The level of the villager.
     *
     * @see net.minecraft.village.VillagerData.getLevel
     */
    val level: Int

    /**
     * The type of the villager (e.g. which biome it was born in, can be seen on its clothing).
     *
     * @see net.minecraft.village.VillagerData.getType
     * @see net.minecraft.village.VillagerType
     */
    val type: Identifier

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        appendSelector.accept("villager.professions", "$profession@$level")
        appendSelector.accept("villager.types", type.toString())
    }
}
