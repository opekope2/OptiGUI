package opekope2.optigui.internal

import net.minecraft.block.entity.*
import net.minecraft.block.enums.ChestType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.SkeletonHorseEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.passive.*
import net.minecraft.entity.vehicle.ChestBoatEntity
import net.minecraft.entity.vehicle.ChestMinecartEntity
import net.minecraft.entity.vehicle.HopperMinecartEntity
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.Nameable
import opekope2.lilac.api.registry.IRegistryLookup
import opekope2.optigui.annotation.BlockEntityProcessor
import opekope2.optigui.annotation.EntityProcessor
import opekope2.optigui.api.interaction.IBlockEntityProcessor
import opekope2.optigui.api.interaction.IEntityProcessor
import opekope2.optigui.api.interaction.IInteractionData
import opekope2.optigui.properties.impl.*
import opekope2.util.comparatorOutputWorkaround
import java.time.LocalDate

private val lookup = IRegistryLookup.getInstance()

private fun BlockEntity.createCommonProperties(): CommonProperties? {
    val world = world ?: return null

    return CommonProperties(
        GeneralProperties(
            name = (this as? Nameable)?.customName?.string,
            biome = lookup.lookupBiomeId(world, pos),
            height = pos.y
        ),
        IndependentProperties(
            date = LocalDate.now()
        )
    )
}

private fun Entity.createCommonProperties(): CommonProperties? {
    val world = entityWorld ?: return null

    return CommonProperties(
        GeneralProperties(
            name = customName?.string,
            biome = lookup.lookupBiomeId(world, blockPos),
            height = blockY
        ),
        IndependentProperties(
            date = LocalDate.now()
        )
    )
}

@BlockEntityProcessor(EnchantingTableBlockEntity::class)
@BlockEntityProcessor(EnderChestBlockEntity::class)
@BlockEntityProcessor(HangingSignBlockEntity::class)
object GenericBlockEntityProcessor : IBlockEntityProcessor<BlockEntity> {
    override fun apply(blockEntity: BlockEntity): IInteractionData? = blockEntity.createCommonProperties()
}

@BlockEntityProcessor(BrewingStandBlockEntity::class)
@BlockEntityProcessor(BarrelBlockEntity::class)
@BlockEntityProcessor(DispenserBlockEntity::class)
@BlockEntityProcessor(DropperBlockEntity::class)
@BlockEntityProcessor(FurnaceBlockEntity::class)
@BlockEntityProcessor(BlastFurnaceBlockEntity::class)
@BlockEntityProcessor(SmokerBlockEntity::class)
@BlockEntityProcessor(HopperBlockEntity::class)
@BlockEntityProcessor(ShulkerBoxBlockEntity::class)
object ComparableBlockEntityProcessor : IBlockEntityProcessor<BlockEntity> {
    override fun apply(blockEntity: BlockEntity): IInteractionData? {
        val screen = MinecraftClient.getInstance().currentScreen

        return CommonRedstoneComparatorProperties(
            commonProperties = blockEntity.createCommonProperties() ?: return null,
            comparatorOutput = (screen as? HandledScreen<*>)?.screenHandler?.comparatorOutputWorkaround ?: 0
        )
    }
}

@EntityProcessor(CamelEntity::class)
@EntityProcessor(SkeletonHorseEntity::class)
@EntityProcessor(ZombieHorseEntity::class)
@EntityProcessor(WanderingTraderEntity::class)
object GenericEntityProcessor : IEntityProcessor<Entity> {
    override fun apply(entity: Entity): IInteractionData? = entity.createCommonProperties()
}

@EntityProcessor(ChestMinecartEntity::class)
@EntityProcessor(HopperMinecartEntity::class)
object ComparableEntityProcessor : IEntityProcessor<Entity> {
    override fun apply(entity: Entity): IInteractionData? {
        val screen = MinecraftClient.getInstance().currentScreen

        return CommonRedstoneComparatorProperties(
            commonProperties = entity.createCommonProperties() ?: return null,
            comparatorOutput = (screen as? HandledScreen<*>)?.screenHandler?.comparatorOutputWorkaround ?: 0
        )
    }
}

@BlockEntityProcessor(BeaconBlockEntity::class)
object BeaconProcessor : IBlockEntityProcessor<BeaconBlockEntity> {
    override fun apply(beacon: BeaconBlockEntity): IInteractionData? {
        return BeaconProperties(
            commonProperties = beacon.createCommonProperties() ?: return null,
            level = beacon.level
        )
    }
}

private val chestTypeEnum = EnumProperty.of("type", ChestType::class.java)

@BlockEntityProcessor(ChestBlockEntity::class)
@BlockEntityProcessor(TrappedChestBlockEntity::class)
object ChestProcessor : IBlockEntityProcessor<ChestBlockEntity> {
    override fun apply(chest: ChestBlockEntity): IInteractionData? {
        val world = chest.world ?: return null
        val state = world.getBlockState(chest.pos)
        val type = state.entries[chestTypeEnum]
        val screen = MinecraftClient.getInstance().currentScreen

        return ChestProperties(
            commonProperties = chest.createCommonProperties() ?: return null,
            redstoneComparatorProperties = RedstoneComparatorProperties(
                comparatorOutput = (screen as? HandledScreen<*>)?.screenHandler?.comparatorOutputWorkaround ?: 0
            ),
            isLarge = type != ChestType.SINGLE
        )
    }
}

@EntityProcessor(ChestBoatEntity::class)
object ChestBoatProcessor : IEntityProcessor<ChestBoatEntity> {
    override fun apply(chestBoat: ChestBoatEntity): IInteractionData? {
        return ChestBoatProperties(
            commonProperties = chestBoat.createCommonProperties() ?: return null,
            variant = chestBoat.variant.getName()
        )
    }
}

@EntityProcessor(HorseEntity::class)
object HorseProcessor : IEntityProcessor<HorseEntity> {
    override fun apply(horse: HorseEntity): IInteractionData? {
        return HorseProperties(
            commonProperties = horse.createCommonProperties() ?: return null,
            horseLikeProperties = HorseLikeProperties(
                hasSaddle = horse.isSaddled
            ),
            variant = horse.variant.asString(),
            marking = horse.marking.name.lowercase()
        )
    }
}

@EntityProcessor(DonkeyEntity::class)
@EntityProcessor(MuleEntity::class)
object DonkeyProcessor : IEntityProcessor<AbstractDonkeyEntity> {
    override fun apply(donkey: AbstractDonkeyEntity): IInteractionData? {
        return DonkeyProperties(
            commonProperties = donkey.createCommonProperties() ?: return null,
            horseLikeProperties = HorseLikeProperties(
                hasSaddle = donkey.isSaddled
            ),
            hasChest = donkey.hasChest()
        )
    }
}

@EntityProcessor(LlamaEntity::class)
@EntityProcessor(TraderLlamaEntity::class)
object LlamaProcessor : IEntityProcessor<LlamaEntity> {
    override fun apply(llama: LlamaEntity): IInteractionData? {
        return LlamaProperties(
            donkeyProperties = DonkeyProperties(
                commonProperties = llama.createCommonProperties() ?: return null,
                horseLikeProperties = HorseLikeProperties(
                    hasSaddle = llama.isSaddled
                ),
                hasChest = llama.hasChest()
            ),
            carpetColor = llama.carpetColor?.getName(),
            variant = llama.variant.asString()
        )
    }
}

@EntityProcessor(VillagerEntity::class)
object VillagerProcessor : IEntityProcessor<VillagerEntity> {
    override fun apply(villager: VillagerEntity): IInteractionData? {
        return VillagerProperties(
            commonProperties = villager.createCommonProperties() ?: return null,
            profession = lookup.lookupVillagerProfessionId(villager.villagerData.profession),
            level = villager.villagerData.level,
            type = lookup.lookupVillagerTypeId(villager.villagerData.type)
        )
    }
}

@BlockEntityProcessor(LecternBlockEntity::class)
object LecternProcessor : IBlockEntityProcessor<LecternBlockEntity> {
    override fun apply(lectern: LecternBlockEntity): IInteractionData? {
        // Workaround, because LecternBlockEntity doesn't sync
        val screen = MinecraftClient.getInstance().currentScreen as? LecternScreen ?: return null

        return LecternProperties(
            commonProperties = lectern.createCommonProperties() ?: return null,
            redstoneComparatorProperties = RedstoneComparatorProperties(
                comparatorOutput = screen.comparatorOutputWorkaround
            ),
            currentPage = screen.pageIndex + 1,
            pageCount = screen.pageCount
        )
    }
}
