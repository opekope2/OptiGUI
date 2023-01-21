package opekope2.optigui.provider

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.village.VillagerProfession
import net.minecraft.world.World

/**
 * Interface defining Minecraft registry search functions.
 */
interface RegistryLookup {
    /**
     * Finds the ID of the given [block] in the registry.
     */
    fun lookupBlockId(block: Block): Identifier

    /**
     * Finds the ID of the given [entity] in the registry.
     */
    fun lookupEntityId(entity: Entity): Identifier

    /**
     * Finds the biome ID at the given world position.
     *
     * @param world The world to look in
     * @param pos The position to lookup biome
     */
    fun lookupBiome(world: World, pos: BlockPos): Identifier

    /**
     * Finds the ID of the given villager [profession] in the registry.
     */
    fun lookupVillagerProfessionId(profession: VillagerProfession): Identifier
}
