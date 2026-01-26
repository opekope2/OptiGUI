package opekope2.optigui.internal.fabric.event_handler

import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import opekope2.optigui.config.config
import opekope2.optigui.interaction.*

internal object FabricAttackHandler : AttackBlockCallback, AttackEntityCallback, ClientPreAttackCallback {
    init {
        AttackBlockCallback.EVENT.register(this)
        AttackEntityCallback.EVENT.register(this)
        ClientPreAttackCallback.EVENT.register(this)
    }

    override fun interact(
        player: Player,
        world: Level,
        hand: InteractionHand,
        pos: BlockPos,
        direction: Direction
    ): InteractionResult {
        if (!world.isClientSide) return InteractionResult.PASS
        if (!config.interactWithAttackKey()) return InteractionResult.PASS

        InteractionManager.prepare(
            BlockInteraction.factory(pos, world.getBlockState(pos), world.getBlockEntity(pos), player, hand)
        )

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
        if (!config.interactWithAttackKey()) return InteractionResult.PASS

        InteractionManager.prepare(EntityInteraction.factory(entity, player, hand))

        return InteractionResult.PASS
    }

    override fun onClientPlayerPreAttack(
        client: Minecraft,
        player: LocalPlayer,
        clickCount: Int
    ): Boolean {
        if (!config.interactWithAttackKey()) return false

        InteractionManager.prepare(
            GeneralInteraction.factory(InteractionTarget.Item(player.mainHandItem), player, InteractionHand.MAIN_HAND)
        )

        return false
    }
}
