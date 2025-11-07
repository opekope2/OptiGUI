package opekope2.optigui.internal.fabric.event_handler

import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.optigui.config.IConfig
import opekope2.optigui.interaction.*

internal object FabricAttackHandler : AttackBlockCallback, AttackEntityCallback, ClientPreAttackCallback {
    init {
        AttackBlockCallback.EVENT.register(this)
        AttackEntityCallback.EVENT.register(this)
        ClientPreAttackCallback.EVENT.register(this)
    }

    override fun interact(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        pos: BlockPos,
        direction: Direction
    ): ActionResult {
        if (!world.isClient) return ActionResult.PASS
        if (!IConfig.get().interactWithAttackKey) return ActionResult.PASS

        InteractionManager.prepare(
            BlockInteraction.factory(pos, world.getBlockState(pos), world.getBlockEntity(pos), player, hand)
        )

        return ActionResult.PASS
    }

    override fun interact(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        entity: Entity,
        hitResult: EntityHitResult?
    ): ActionResult {
        if (!world.isClient) return ActionResult.PASS
        if (!IConfig.get().interactWithAttackKey) return ActionResult.PASS

        InteractionManager.prepare(EntityInteraction.factory(entity, player, hand))

        return ActionResult.PASS
    }

    override fun onClientPlayerPreAttack(
        client: MinecraftClient,
        player: ClientPlayerEntity,
        clickCount: Int
    ): Boolean {
        if (!IConfig.get().interactWithAttackKey) return false

        InteractionManager.prepare(
            GeneralInteraction.factory(InteractionTarget.Item(player.mainHandStack), player, Hand.MAIN_HAND)
        )

        return false
    }
}
