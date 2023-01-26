package opekope2.optiglue.mc_1_19

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.util.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.village.VillagerProfession
import net.minecraft.world.World
import opekope2.optigui.provider.RegistryLookup
import kotlin.jvm.optionals.getOrNull

internal class RegistryLookupImpl : RegistryLookup {
    override fun lookupBlockId(block: Block): Identifier = Registry.BLOCK.getId(block)
    override fun lookupEntityId(entity: Entity): Identifier = Registry.ENTITY_TYPE.getId(entity.type)

    @OptIn(ExperimentalStdlibApi::class)
    override fun lookupBiome(world: World, pos: BlockPos): Identifier =
        world.getBiome(pos).key.getOrNull()?.value ?: throw RuntimeException("Cannot load biome at $pos (world=$world)!")

    override fun lookupVillagerProfessionId(profession: VillagerProfession): Identifier =
        Registry.VILLAGER_PROFESSION.getId(profession)
}
