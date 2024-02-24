package opekope2.optigui.util

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

/**
 * Finds the ID of the given block in the registry.
 */
val Block.identifier: Identifier
    get() = Registries.BLOCK.getId(this)

/**
 * Finds the ID of the given entity in the registry.
 */
val Entity.identifier: Identifier
    get() = Registries.ENTITY_TYPE.getId(type)

/**
 * Finds the biome ID at the given world position.
 *
 * @param pos The position to look up the biome
 */
fun World.getBiomeId(pos: BlockPos) = getBiome(pos).key.getOrNull()?.value
    ?: throw RuntimeException("Cannot load biome at $pos in world $this!")
