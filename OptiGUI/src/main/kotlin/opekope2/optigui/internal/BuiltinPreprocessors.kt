package opekope2.optigui.internal

import net.minecraft.block.entity.*
import net.minecraft.block.enums.ChestType
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.SkeletonHorseEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.passive.*
import net.minecraft.entity.vehicle.ChestMinecartEntity
import net.minecraft.entity.vehicle.HopperMinecartEntity
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.Nameable
import opekope2.optigui.InitializerContext
import opekope2.optigui.mixin.BeaconBlockEntityAccessorMixin
import opekope2.optigui.properties.*
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService

@Suppress("unused")
internal fun initializePreprocessors(context: InitializerContext) {
    context.registerPreprocessor<BrewingStandBlockEntity>(::processCommon)

    context.registerPreprocessor<EnchantingTableBlockEntity>(::processCommon)

    context.registerPreprocessor<FurnaceBlockEntity>(::processCommon)
    context.registerPreprocessor<BlastFurnaceBlockEntity>(::processCommon)
    context.registerPreprocessor<SmokerBlockEntity>(::processCommon)

    context.registerPreprocessor<HopperBlockEntity>(::processCommon)
    context.registerPreprocessor<HopperMinecartEntity>(::processCommon)

    context.registerPreprocessor<ChestBlockEntity>(::processChest)
    context.registerPreprocessor<TrappedChestBlockEntity>(::processChest)
    context.registerPreprocessor<EnderChestBlockEntity>(::processCommon)
    context.registerPreprocessor<BarrelBlockEntity>(::processCommon)
    context.registerPreprocessor<ChestMinecartEntity>(::processCommon)

    context.registerPreprocessor<BeaconBlockEntity>(::processBeacon)

    context.registerPreprocessor<VillagerEntity>(::processVillager)
    context.registerPreprocessor<WanderingTraderEntity>(::processCommon)

    context.registerPreprocessor<DispenserBlockEntity>(::processCommon)
    context.registerPreprocessor<DropperBlockEntity>(::processCommon)

    context.registerPreprocessor<HorseEntity>(::processCommon)
    context.registerPreprocessor<DonkeyEntity>(::processDonkey)
    context.registerPreprocessor<MuleEntity>(::processDonkey)
    context.registerPreprocessor<LlamaEntity>(::processLlama)
    context.registerPreprocessor<TraderLlamaEntity>(::processLlama)
    context.registerPreprocessor<ZombieHorseEntity>(::processCommon)
    context.registerPreprocessor<SkeletonHorseEntity>(::processCommon)

    context.registerPreprocessor<ShulkerBoxBlockEntity>(::processCommon)

    context.registerPreprocessor<LecternBlockEntity>(::processCommon)

    context.registerPreprocessor<HangingSignBlockEntity>(::processHangingSign)
}

private val lookup: RegistryLookupService by lazy(::getService)

private fun processCommon(blockEntity: BlockEntity): Any? {
    val world = blockEntity.world ?: return null

    return DefaultProperties(
        container = lookup.lookupBlockId(world.getBlockState(blockEntity.pos).block),
        name = (blockEntity as? Nameable)?.customName?.string,
        biome = lookup.lookupBiomeId(world, blockEntity.pos),
        height = blockEntity.pos.y
    )
}

fun processCommon(entity: Entity): Any? {
    val world = entity.entityWorld ?: return null

    return DefaultProperties(
        container = lookup.lookupEntityId(entity),
        name = entity.customName?.string,
        biome = lookup.lookupBiomeId(world, entity.blockPos),
        height = entity.blockY
    )
}

private fun processBeacon(beacon: BeaconBlockEntity): Any? {
    val world = beacon.world ?: return null

    return BeaconProperties(
        container = lookup.lookupBlockId(world.getBlockState(beacon.pos).block),
        name = (beacon as? Nameable)?.customName?.string,
        biome = lookup.lookupBiomeId(world, beacon.pos),
        height = beacon.pos.y,
        level = (beacon as BeaconBlockEntityAccessorMixin).level
    )
}

private val chestTypeEnum = EnumProperty.of("type", ChestType::class.java)

private fun processChest(chest: ChestBlockEntity): Any? {
    val world = chest.world ?: return null
    val state = world.getBlockState(chest.pos)
    val type = state.entries[chestTypeEnum]

    return ChestProperties(
        container = lookup.lookupBlockId(world.getBlockState(chest.pos).block),
        name = chest.customName?.string,
        biome = lookup.lookupBiomeId(world, chest.pos),
        height = chest.pos.y,
        isLarge = type != ChestType.SINGLE
    )
}

private fun processDonkey(donkey: AbstractDonkeyEntity): Any? {
    val world = donkey.entityWorld ?: return null

    return DonkeyProperties(
        container = lookup.lookupEntityId(donkey),
        name = donkey.customName?.string,
        biome = lookup.lookupBiomeId(world, donkey.blockPos),
        height = donkey.blockY,
        hasChest = donkey.hasChest()
    )
}

private fun processLlama(llama: LlamaEntity): Any? {
    val world = llama.entityWorld ?: return null

    return LlamaProperties(
        container = lookup.lookupEntityId(llama),
        name = llama.customName?.string,
        biome = lookup.lookupBiomeId(world, llama.blockPos),
        height = llama.blockY,
        carpetColor = llama.carpetColor?.getName(),
        hasChest = llama.hasChest()
    )
}

private fun processVillager(villager: VillagerEntity): Any? {
    val world = villager.entityWorld ?: return null

    return VillagerProperties(
        container = lookup.lookupEntityId(villager),
        name = villager.customName?.string,
        biome = lookup.lookupBiomeId(world, villager.blockPos),
        height = villager.blockY,
        level = villager.villagerData.level,
        profession = lookup.lookupVillagerProfessionId(villager.villagerData.profession)
    )
}

private fun processHangingSign(sign: HangingSignBlockEntity): Any? {
    val world = sign.world ?: return null

    return DefaultProperties(
        container = lookup.lookupBlockId(world.getBlockState(sign.pos).block),
        name = null,
        biome = lookup.lookupBiomeId(world, sign.pos),
        height = sign.pos.y
    )
}
