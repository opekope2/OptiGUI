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
import net.minecraft.registry.Registries
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.Nameable
import opekope2.optigui.InitializerContext
import opekope2.optigui.properties.*
import opekope2.optigui.util.comparatorOutput
import opekope2.optigui.util.getBiomeId
import opekope2.optigui.util.identifier

@Suppress("unused")
internal fun initializePreprocessors(context: InitializerContext) {
    context.registerPreprocessor<BrewingStandBlockEntity>(::processCommonComparable)

    context.registerPreprocessor<EnchantingTableBlockEntity>(::processCommon)

    context.registerPreprocessor<FurnaceBlockEntity>(::processCommonComparable)
    context.registerPreprocessor<BlastFurnaceBlockEntity>(::processCommonComparable)
    context.registerPreprocessor<SmokerBlockEntity>(::processCommonComparable)

    context.registerPreprocessor<HopperBlockEntity>(::processCommonComparable)
    context.registerPreprocessor<HopperMinecartEntity>(::processCommonComparable)

    context.registerPreprocessor<ChestBlockEntity>(::processChest)
    context.registerPreprocessor<TrappedChestBlockEntity>(::processChest)
    context.registerPreprocessor<EnderChestBlockEntity>(::processCommon)
    context.registerPreprocessor<BarrelBlockEntity>(::processCommonComparable)
    context.registerPreprocessor<ChestMinecartEntity>(::processCommonComparable)
    context.registerPreprocessor<ChestBoatEntity>(::processChestBoat)

    context.registerPreprocessor<BeaconBlockEntity>(::processBeacon)

    context.registerPreprocessor<VillagerEntity>(::processVillager)
    context.registerPreprocessor<WanderingTraderEntity>(::processCommon)

    context.registerPreprocessor<DispenserBlockEntity>(::processCommonComparable)
    context.registerPreprocessor<DropperBlockEntity>(::processCommonComparable)

    context.registerPreprocessor<HorseEntity>(::processCommon)
    context.registerPreprocessor<CamelEntity>(::processCommon)
    context.registerPreprocessor<DonkeyEntity>(::processDonkey)
    context.registerPreprocessor<MuleEntity>(::processDonkey)
    context.registerPreprocessor<LlamaEntity>(::processLlama)
    context.registerPreprocessor<TraderLlamaEntity>(::processLlama)
    context.registerPreprocessor<ZombieHorseEntity>(::processCommon)
    context.registerPreprocessor<SkeletonHorseEntity>(::processCommon)

    context.registerPreprocessor<ShulkerBoxBlockEntity>(::processCommonComparable)

    context.registerPreprocessor<LecternBlockEntity>(::processLectern)

    context.registerPreprocessor<HangingSignBlockEntity>(::processHangingSign)
}

private fun processCommon(blockEntity: BlockEntity): Any? {
    val world = blockEntity.world ?: return null

    return DefaultProperties(
        container = world.getBlockState(blockEntity.pos).block.identifier,
        name = (blockEntity as? Nameable)?.customName?.string,
        biome = world.getBiomeId(blockEntity.pos),
        height = blockEntity.pos.y
    )
}

private fun processCommonComparable(blockEntity: BlockEntity): Any? {
    val world = blockEntity.world ?: return null
    val screen = MinecraftClient.getInstance().currentScreen

    return CommonComparatorProperties(
        container = world.getBlockState(blockEntity.pos).block.identifier,
        name = (blockEntity as? Nameable)?.customName?.string,
        biome = world.getBiomeId(blockEntity.pos),
        height = blockEntity.pos.y,
        comparatorOutput = (screen as? HandledScreen<*>)?.screenHandler?.comparatorOutput ?: 0
    )
}

fun processCommon(entity: Entity): Any? {
    val world = entity.entityWorld ?: return null

    return DefaultProperties(
        container = entity.identifier,
        name = entity.customName?.string,
        biome = world.getBiomeId(entity.blockPos),
        height = entity.blockY
    )
}

private fun processCommonComparable(entity: Entity): Any? {
    val world = entity.world ?: return null
    val screen = MinecraftClient.getInstance().currentScreen

    return CommonComparatorProperties(
        container = entity.identifier,
        name = entity.customName?.string,
        biome = world.getBiomeId(entity.blockPos),
        height = entity.blockY,
        comparatorOutput = (screen as? HandledScreen<*>)?.screenHandler?.comparatorOutput ?: 0
    )
}

private fun processBeacon(beacon: BeaconBlockEntity): Any? {
    val world = beacon.world ?: return null

    return BeaconProperties(
        container = world.getBlockState(beacon.pos).block.identifier,
        name = (beacon as? Nameable)?.customName?.string,
        biome = world.getBiomeId(beacon.pos),
        height = beacon.pos.y,
        level = beacon.level
    )
}

private val chestTypeEnum = EnumProperty.of("type", ChestType::class.java)

private fun processChest(chest: ChestBlockEntity): Any? {
    val world = chest.world ?: return null
    val state = world.getBlockState(chest.pos)
    val type = state.entries[chestTypeEnum]
    val screen = MinecraftClient.getInstance().currentScreen

    return ChestProperties(
        container = world.getBlockState(chest.pos).block.identifier,
        name = chest.customName?.string,
        biome = world.getBiomeId(chest.pos),
        height = chest.pos.y,
        isLarge = type != ChestType.SINGLE,
        comparatorOutput = (screen as? HandledScreen<*>)?.screenHandler?.comparatorOutput ?: 0
    )
}

private fun processChestBoat(chestBoat: ChestBoatEntity): Any? {
    val world = chestBoat.entityWorld ?: return null

    return ChestBoatProperties(
        container = chestBoat.identifier,
        name = (chestBoat as? Nameable)?.customName?.string,
        biome = world.getBiomeId(chestBoat.blockPos),
        height = chestBoat.blockY,
        variant = chestBoat.variant.getName()
    )
}

private fun processDonkey(donkey: AbstractDonkeyEntity): Any? {
    val world = donkey.entityWorld ?: return null

    return DonkeyProperties(
        container = donkey.identifier,
        name = donkey.customName?.string,
        biome = world.getBiomeId(donkey.blockPos),
        height = donkey.blockY,
        hasChest = donkey.hasChest()
    )
}

private fun processLlama(llama: LlamaEntity): Any? {
    val world = llama.entityWorld ?: return null

    return LlamaProperties(
        container = llama.identifier,
        name = llama.customName?.string,
        biome = world.getBiomeId(llama.blockPos),
        height = llama.blockY,
        carpetColor = llama.carpetColor?.getName(),
        hasChest = llama.hasChest()
    )
}

private fun processVillager(villager: VillagerEntity): Any? {
    val world = villager.entityWorld ?: return null

    return VillagerProperties(
        container = villager.identifier,
        name = villager.customName?.string,
        biome = world.getBiomeId(villager.blockPos),
        height = villager.blockY,
        level = villager.villagerData.level,
        profession = Registries.VILLAGER_PROFESSION.getId(villager.villagerData.profession),
        type = Registries.VILLAGER_TYPE.getId(villager.villagerData.type)
    )
}

private fun processHangingSign(sign: HangingSignBlockEntity): Any? {
    val world = sign.world ?: return null

    return DefaultProperties(
        container = world.getBlockState(sign.pos).block.identifier,
        name = null,
        biome = world.getBiomeId(sign.pos),
        height = sign.pos.y
    )
}

private fun processLectern(lectern: LecternBlockEntity): Any? {
    val world = lectern.world ?: return null
    // Workaround, because LecternBlockEntity doesn't sync
    val screen = MinecraftClient.getInstance().currentScreen

    return LecternProperties(
        container = world.getBlockState(lectern.pos).block.identifier,
        name = null,
        biome = world.getBiomeId(lectern.pos),
        height = lectern.pos.y,
        currentPage = (screen as? LecternScreen)?.pageIndex?.plus(1) ?: return null,
        pageCount = (screen as? LecternScreen)?.pageCount ?: return null,
        comparatorOutput = (screen as? LecternScreen)?.comparatorOutput ?: 0
    )
}
