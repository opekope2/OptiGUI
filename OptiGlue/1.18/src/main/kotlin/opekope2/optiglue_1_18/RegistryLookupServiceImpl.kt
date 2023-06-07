package opekope2.optiglue_1_18

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.village.VillagerProfession
import net.minecraft.world.World
import opekope2.optigui.service.RegistryLookupService
import kotlin.jvm.optionals.getOrNull

internal class RegistryLookupServiceImpl : RegistryLookupService {
    override fun lookupBlockId(block: Block): Identifier = Registry.BLOCK.getId(block)
    override fun lookupEntityId(entity: Entity): Identifier = Registry.ENTITY_TYPE.getId(entity.type)

    override fun lookupBiomeId(world: World, pos: BlockPos): Identifier =
        world.getBiomeKey(pos).getOrNull()?.value ?: throw RuntimeException("Cannot load biome at $pos (world=$world)!")

    override fun lookupVillagerProfessionId(profession: VillagerProfession): Identifier =
        Registry.VILLAGER_PROFESSION.getId(profession)
}
