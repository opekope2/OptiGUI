package opekope2.optigui.internal.neoforge.event_handler

import net.minecraft.world.InteractionHand
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import opekope2.optigui.config.IConfig
import opekope2.optigui.interaction.*

internal object NeoForgeAttackHandler {
    @SubscribeEvent
    fun attackBlock(event: PlayerInteractEvent.LeftClickBlock) {
        if (!event.level.isClientSide) return
        if (!IConfig.get().interactWithAttackKey) return

        val pos = event.pos
        val world = event.level
        val player = event.entity
        val hand = event.hand

        InteractionManager.prepare(
            BlockInteraction.factory(pos, world.getBlockState(pos), world.getBlockEntity(pos), player, hand)
        )
    }

    @SubscribeEvent
    fun attackEntity(event: AttackEntityEvent) {
        if (!event.entity.level().isClientSide) return
        if (!IConfig.get().interactWithAttackKey) return

        InteractionManager.prepare(EntityInteraction.factory(event.target, event.entity, InteractionHand.MAIN_HAND))
    }

    @SubscribeEvent
    fun attackWithItem(event: PlayerInteractEvent.LeftClickEmpty) {
        if (!event.level.isClientSide) return
        if (!IConfig.get().interactWithAttackKey) return

        val player = event.entity

        InteractionManager.prepare(
            GeneralInteraction.factory(InteractionTarget.Item(player.mainHandItem), player, InteractionHand.MAIN_HAND)
        )
    }
}
