package opekope2.optigui.filter

import com.mojang.serialization.Encoder
import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.dimension.DimensionType
import opekope2.optigui.interaction.BlockInteractionData
import opekope2.optigui.interaction.EntityInteractionData
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.screen.IRedstoneComparatorOutputGetterScreen
import opekope2.optigui.util.MOD_ID
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

/**
 * Interaction component getters.
 */
enum class InteractionComponentGetter {
    /**
     * Interacted block component getter.
     */
    BLOCK {
        override val componentIds
            get() = blockComponentIds

        override fun getComponent(interaction: Interaction, componentId: Identifier) = when {
            interaction.data !is BlockInteractionData -> null
            componentId == customDataComponentId -> interaction.data.blockEntity?.createNbtWithId(interaction.data.world.registryManager)
            componentId == blockStateComponentId -> BlockState.CODEC
                .encodeStart(NbtOps.INSTANCE, interaction.data.blockState).result().getOrNull() as? NbtCompound

            else -> null
        }
    },

    /**
     * Interacted entity component getter.
     */
    ENTITY {
        override val componentIds
            get() = entityComponentIds

        override fun getComponent(interaction: Interaction, componentId: Identifier) = when {
            interaction.data !is EntityInteractionData -> null
            componentId == customDataComponentId -> interaction.data.entity.writeNbt(NbtCompound())
            else -> null
        }
    },

    /**
     * Ridden entity component getter.
     */
    VEHICLE {
        override val componentIds
            get() = entityComponentIds

        override fun getComponent(interaction: Interaction, componentId: Identifier) = when (componentId) {
            customDataComponentId -> interaction.playerData.vehicle?.writeNbt(NbtCompound())
            else -> null
        }
    },

    /**
     * Player component getter.
     */
    PLAYER {
        override val componentIds
            get() = entityComponentIds

        override fun getComponent(interaction: Interaction, componentId: Identifier) = when (componentId) {
            customDataComponentId -> interaction.playerData.player.writeNbt(NbtCompound())
            else -> null
        }
    },

    /**
     * Item component getter.
     */
    ITEM {
        override val componentIds: Set<Identifier>
            get() = Registries.DATA_COMPONENT_TYPE.ids

        override fun getComponent(interaction: Interaction, componentId: Identifier): NbtCompound? {
            return when (componentId) {
                customDataComponentId -> {
                    val componentType = Registries.DATA_COMPONENT_TYPE.get(componentId) ?: return null
                    val component = interaction.data.item[componentType] ?: return null

                    @Suppress("UNCHECKED_CAST")
                    val encoder = componentType.codec as? Encoder<Any> ?: return null
                    encoder.encodeStart(NbtOps.INSTANCE, component)?.result()?.getOrNull() as? NbtCompound
                }

                else -> null
            }
        }
    },

    /**
     * World component getter.
     */
    WORLD {
        override val componentIds
            get() = worldComponentIds

        override fun getComponent(interaction: Interaction, componentId: Identifier) = when (componentId) {
            valueComponentId -> NbtCompound().apply {
                interaction.data.world.apply {
                    // TODO update when porting to a different version
                    putInt("ambientDarkness", ambientDarkness)
                    // asString - debug info
                    // biomeAccess - no properties
                    putInt("bottomSectionCoord", bottomSectionCoord)
                    putInt("bottomY", bottomY)
                    // brewingRecipeRegistry - no properties
                    // chunkManager - no additional properties
                    putInt("verticalSections", countVerticalSections())
                    // damageSources - irrelevant? data pack detection?
                    putInt("difficulty", difficulty.ordinal)
                    put("dimension", DimensionType.CODEC.encodeStart(NbtOps.INSTANCE, dimension).getOrThrow())
                    // dimensionEntry - use dimension
                    // enabledFeatures - info unobtainable
                    // fluidTickScheduler - irrelevant
                    put("gameRules", gameRules.toNbt())
                    // getClass - irrelevant
                    // getEntityLookup - irrelevant
                    // getRandom - irrelevant
                    // hashCode - irrelevant
                    putInt("height", height)
                    // increaseAndGetMapId - mutates world
                    // isClient - true
                    putBoolean("isDay", isDay)
                    putBoolean("isDebugWorld", isDebugWorld)
                    putBoolean("isDifficultyLocked", levelProperties.isDifficultyLocked) // from levelProperties
                    putBoolean("isHardcore", levelProperties.isHardcore) // from levelProperties
                    putBoolean("isNight", isNight)
                    putBoolean("isRaining", isRaining)
                    putBoolean("isSavingDisabled", isSavingDisabled)
                    putBoolean("isThundering", isThundering)
                    // levelProperties - no additional info
                    // lightingProvider - no additional info
                    putInt("loadedChunkCount", chunkManager.loadedChunkCount) // from chunkManager
                    putLong("lunarTime", lunarTime)
                    putInt("maxLightLevel", maxLightLevel)
                    putInt("moonPhase", moonPhase)
                    putFloat("moonSize", moonSize)
                    // players - irrelevant? too much info
                    // profiler - irrelevant
                    // profilerSupplier - irrelevant
                    // recipeManager - irrelevant
                    // registryKey - irrelevant
                    // registryManager - irrelevant
                    // scoreboard - maybe later
                    putInt("seaLevel", seaLevel)
                    // server - irrelevant
                    putFloat("spawnAngle", spawnAngle)
                    put("spawnPos", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, spawnPos).getOrThrow())
                    put("tickManager", NbtCompound().apply {
                        putBoolean("isFrozen", tickManager.isFrozen)
                        putBoolean("isStepping", tickManager.isStepping)
                        putBoolean("shouldTick", tickManager.shouldTick())
                        putFloat("millisPerTick", tickManager.millisPerTick)
                        putFloat("tickRate", tickManager.tickRate)
                        putInt("stepTicks", tickManager.stepTicks)
                        putLong("nanosPerTick", tickManager.nanosPerTick)
                    })
                    // tickOrder - mutates world
                    putLong("time", time)
                    putLong("timeOfDay", timeOfDay)
                    // toString - irrelevant
                    putInt("topSectionCoord", topSectionCoord)
                    putInt("topY", topY)
                    put("worldBorder", NbtCompound().apply {
                        putDouble("boundEast", worldBorder.boundEast)
                        putDouble("boundNorth", worldBorder.boundNorth)
                        putDouble("boundSouth", worldBorder.boundSouth)
                        putDouble("boundWest", worldBorder.boundWest)
                        putDouble("centerX", worldBorder.centerX)
                        putDouble("centerZ", worldBorder.centerZ)
                        putDouble("damagePerBlock", worldBorder.damagePerBlock)
                        putInt("maxRadius", worldBorder.maxRadius)
                        putDouble("safeZone", worldBorder.safeZone)
                        putDouble("shrinkingSpeed", worldBorder.shrinkingSpeed)
                        putDouble("size", worldBorder.size)
                        putDouble("sizeLerpTarget", worldBorder.sizeLerpTarget)
                        putLong("sizeLerpTime", worldBorder.sizeLerpTime)
                        putString("stage", worldBorder.stage.name)
                        putInt("warningBlocks", worldBorder.warningBlocks)
                        putInt("warningTime", worldBorder.warningTime)
                    })
                }
            }

            else -> null
        }
    },

    /**
     * Interaction component getter.
     */
    INTERACTION {
        override val componentIds
            get() = interactionComponentIds

        override fun getComponent(interaction: Interaction, componentId: Identifier) = when (componentId) {
            valueComponentId -> NbtCompound().apply {
                putString("hand", interaction.playerData.hand.name)
                interaction.extraData?.get()?.let { put("extra", it) }
            }

            extraComponentId -> NbtCompound().apply {
                val redstoneComparatorOutputGetter = interaction.screen as? IRedstoneComparatorOutputGetterScreen
                if (redstoneComparatorOutputGetter != null) {
                    put("RedstoneComparator", NbtCompound().apply {
                        putInt("output", redstoneComparatorOutputGetter.redstoneComparatorOutput)
                    })
                }
                put("Time", NbtCompound().apply {
                    val now = LocalDateTime.now()
                    putInt("year", now.year)
                    putString("month", now.month.name)
                    putInt("day", now.dayOfMonth)
                    putString("weekDay", now.dayOfWeek.name)
                    putInt("hour", now.hour)
                    putInt("minute", now.minute)
                    putInt("second", now.second)
                })
            }

            else -> null
        }
    };

    /**
     * Gets the supported component IDs of the interaction component getter.
     */
    abstract val componentIds: Set<Identifier>

    /**
     * Serializes an interaction component to NBT. May return `null`.
     *
     * @param interaction The interaction to get the component of
     * @param componentId The component to serialize
     */
    abstract fun getComponent(interaction: Interaction, componentId: Identifier): NbtCompound?

    /**
     * Serializes every component supported by the interaction component getter.
     *
     * @param interaction The interaction to get the components of
     */
    fun getComponents(interaction: Interaction): Map<Identifier, NbtCompound> {
        val components = mutableMapOf<Identifier, NbtCompound>()

        for (componentId in componentIds) {
            val component = getComponent(interaction, componentId)
            if (component != null) {
                components[componentId] = component
            }
        }

        return components
    }

    companion object {
        private val customDataComponentId = Identifier.ofVanilla("custom_data")
        private val blockStateComponentId = Identifier.of(MOD_ID, "block_state")
        private val valueComponentId = Identifier.of(MOD_ID, "value")
        private val extraComponentId = Identifier.of(MOD_ID, "extra")

        private val blockComponentIds = setOf(customDataComponentId, blockStateComponentId)
        private val entityComponentIds = setOf(customDataComponentId)
        private val worldComponentIds = setOf(valueComponentId)
        private val interactionComponentIds = setOf(valueComponentId, extraComponentId)

        private val entriesByName = InteractionComponentGetter.entries.associateBy(InteractionComponentGetter::name)

        /**
         * Gets an interaction component getter by name or `null`, if it's not found, instead of throwing an exception.
         *
         * @param name The enum name of the interaction component getter
         */
        @JvmStatic
        operator fun get(name: String) = entriesByName[name]
    }
}
