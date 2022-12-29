package opekope2.optigui.provider

import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.village.VillagerProfession
import net.minecraft.world.World

interface IRegistryLookupProvider {
    fun lookupBlockId(block: Block): Identifier?
    fun lookupBiome(world: World, pos: BlockPos): Identifier?
    fun lookupVillagerProfessionId(profession: VillagerProfession): Identifier?
}
