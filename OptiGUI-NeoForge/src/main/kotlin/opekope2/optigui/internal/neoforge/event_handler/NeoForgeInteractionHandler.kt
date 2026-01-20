package opekope2.optigui.internal.neoforge.event_handler

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import opekope2.optigui.interaction.*

internal object NeoForgeInteractionHandler {
    @SubscribeEvent
    fun interactWithBlock(event: PlayerInteractEvent.RightClickBlock) {
        if (!event.level.isClientSide) return

        val world = event.level
        val blockPos = event.pos
        val blockState = world.getBlockState(blockPos)
        val blockEntity = world.getBlockEntity(blockPos)
        val player = event.entity
        val hand = event.hand

        InteractionManager.prepare(BlockInteraction.factory(blockPos, blockState, blockEntity, player, hand))
    }

    @SubscribeEvent
    fun interactWithEntity(event: PlayerInteractEvent.EntityInteractSpecific) {
        if (!event.level.isClientSide) return

        val entity = event.target
        val player = event.entity
        val hand = event.hand

        InteractionManager.prepare(EntityInteraction.factory(entity, player, hand))
    }

    @SubscribeEvent
    fun interactWithItem(event: PlayerInteractEvent.RightClickItem) {
        if (!event.level.isClientSide) return

        val player = event.entity
        val hand = event.hand
        val stack = player.getItemInHand(hand)

        InteractionManager.prepare(GeneralInteraction.factory(InteractionTarget.Item(stack), player, hand))
    }
}
