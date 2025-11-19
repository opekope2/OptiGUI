package opekope2.optigui.internal.fabric.event_handler

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import opekope2.optigui.interaction.*

internal object FabricInteractionHandler : UseBlockCallback, UseEntityCallback, UseItemCallback {
    init {
        UseBlockCallback.EVENT.register(this)
        UseEntityCallback.EVENT.register(this)
        UseItemCallback.EVENT.register(this)
    }

    override fun interact(
        player: Player,
        world: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (!world.isClientSide) return InteractionResult.PASS

        val blockPos = hitResult.blockPos
        val blockState = world.getBlockState(blockPos)
        val blockEntity = world.getBlockEntity(blockPos)

        InteractionManager.prepare(BlockInteraction.factory(blockPos, blockState, blockEntity, player, hand))

        return InteractionResult.PASS
    }

    override fun interact(
        player: Player,
        world: Level,
        hand: InteractionHand,
        entity: Entity,
        hitResult: EntityHitResult?
    ): InteractionResult {
        if (!world.isClientSide) return InteractionResult.PASS

        InteractionManager.prepare(EntityInteraction.factory(entity, player, hand))

        return InteractionResult.PASS
    }

    override fun interact(player: Player, world: Level, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        val result = InteractionResultHolder.pass(stack)

        if (!world.isClientSide) return result

        InteractionManager.prepare(GeneralInteraction.factory(InteractionTarget.Item(stack), player, hand))

        return result
    }
}
