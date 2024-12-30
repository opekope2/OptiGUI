package opekope2.optigui.interaction.data

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionType
import opekope2.optigui.util.INbtConvertible
import opekope2.optigui.util.encode
import opekope2.optigui.util.getBiomeId
import java.util.function.Supplier

/**
 * Details about an interaction.
 */
sealed interface IInteractionData : INbtConvertible {
    /**
     * The identifier of the interacted container.
     */
    val id: Identifier

    /**
     * The interaction position.
     */
    val blockPos: BlockPos

    /**
     * The item the player interacted with.
     */
    val item: ItemStack

    /**
     * Details about the interacting player.
     */
    val playerData: InteractionPlayerData

    /**
     * Extra details about the interaction. May be mutable.
     */
    val extraData: Supplier<NbtCompound>?

    /**
     * The world the interaction happened in.
     */
    val world: World
        get() = playerData.player.entityWorld

    override fun writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        compound.putString("container", id.toString())
        compound.encode("pos", blockPos, BlockPos.CODEC, lookup)
        compound.putString("biome", world.getBiomeId(blockPos).toString())
        compound.put("item", item.encodeAllowEmpty(lookup))
        playerData.writeNbt(compound, lookup)
        extraData?.get()?.let { compound.put("extra", it) }
        compound.put("world", createWorldNbt())
        // TODO structures
    }

    private fun createWorldNbt() = NbtCompound().apply {
        world.apply {
            // TODO update when porting to a different version
            putInt("ambient_darkness", ambientDarkness)
            // asString - debug info
            // biomeAccess - no properties
            putInt("bottom_section_coord", bottomSectionCoord)
            putInt("bottom_y", bottomY)
            // brewingRecipeRegistry - no properties
            // chunkManager - no additional properties
            putInt("vertical_section_count", countVerticalSections())
            // damageSources - irrelevant? data pack detection?
            putInt("difficulty", difficulty.ordinal)
            encode("dimension_type", dimension, DimensionType.CODEC, registryManager)
            putString("dimension", dimensionEntry.idAsString)
            // enabledFeatures - info unobtainable
            // fluidTickScheduler - irrelevant
            // gameRules - not synced
            // getClass - irrelevant
            // getEntityLookup - irrelevant
            // getRandom - irrelevant
            // hashCode - irrelevant
            putInt("height", height)
            // increaseAndGetMapId - mutates world
            // isClient - true
            putBoolean("is_day", isDay)
            putBoolean("is_debug_world", isDebugWorld)
            putBoolean("is_difficulty_locked", levelProperties.isDifficultyLocked) // from levelProperties
            putBoolean("is_hardcore", levelProperties.isHardcore) // from levelProperties
            putBoolean("is_night", isNight)
            putBoolean("is_raining", isRaining)
            putBoolean("is_saving_disabled", isSavingDisabled)
            putBoolean("is_thundering", isThundering)
            // levelProperties - no additional info
            // lightingProvider - no additional info
            putInt("loaded_chunk_count", chunkManager.loadedChunkCount) // from chunkManager
            putLong("lunar_time", lunarTime)
            putInt("max_light_level", maxLightLevel)
            putInt("moon_phase", moonPhase)
            putFloat("moon_size", moonSize)
            // players - irrelevant? too much info
            // profiler - irrelevant
            // profilerSupplier - irrelevant
            // recipeManager - irrelevant
            // registryKey - irrelevant
            // registryManager - irrelevant
            // scoreboard - maybe later
            putInt("sea_level", seaLevel)
            // server - irrelevant
            putFloat("spawn_angle", spawnAngle)
            encode("spawn_pos", spawnPos, BlockPos.CODEC, registryManager)
            put("tick_manager", NbtCompound().apply {
                putBoolean("is_frozen", tickManager.isFrozen)
                putBoolean("is_stepping", tickManager.isStepping)
                putBoolean("should_tick", tickManager.shouldTick())
                putFloat("millis_per_tick", tickManager.millisPerTick)
                putFloat("tick_rate", tickManager.tickRate)
                putInt("step_ticks", tickManager.stepTicks)
                putLong("nanos_per_tick", tickManager.nanosPerTick)
            })
            // tickOrder - mutates world
            putLong("time", time)
            putLong("time_of_day", timeOfDay)
            // toString - irrelevant
            putInt("top_section_coord", topSectionCoord)
            putInt("top_y", topY)
            put("border", NbtCompound().apply {
                // Mojmap keys, because it makes more sense
                putDouble("max_x", worldBorder.boundEast)
                putDouble("min_z", worldBorder.boundNorth)
                putDouble("max_z", worldBorder.boundSouth)
                putDouble("min_x", worldBorder.boundWest)
                putDouble("center_x", worldBorder.centerX)
                putDouble("center_z", worldBorder.centerZ)
                putDouble("damage_per_block", worldBorder.damagePerBlock)
                putInt("absolute_max_size", worldBorder.maxRadius)
                putDouble("damage_safe_zone", worldBorder.safeZone)
                putDouble("lerp_speed", worldBorder.shrinkingSpeed)
                putDouble("size", worldBorder.size)
                putDouble("lerp_target", worldBorder.sizeLerpTarget)
                putLong("lerp_remaining_time", worldBorder.sizeLerpTime)
                putString("status", worldBorder.stage.name)
                putInt("warning_blocks", worldBorder.warningBlocks)
                putInt("warning_time", worldBorder.warningTime)
            })
        }
    }
}
