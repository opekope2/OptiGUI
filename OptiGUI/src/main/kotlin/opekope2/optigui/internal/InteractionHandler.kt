package opekope2.optigui.internal

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.service.InteractionService
import opekope2.optigui.service.getService

internal object InteractionHandler : UseBlockCallback, UseEntityCallback {
    private val interactor = getService<InteractionService>()

    override fun interact(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        if (world.isClient) {
            val blockEntity = world.getBlockEntity(hitResult.blockPos)
            val target =
                if (blockEntity != null) InteractionTarget.BlockEntity(blockEntity)
                else InteractionTarget.None

            interactor.interact(player, world, hand, target, hitResult)
        }

        return ActionResult.PASS
    }

    override fun interact(
        player: PlayerEntity, world: World, hand: Hand, entity: Entity, hitResult: EntityHitResult?
    ): ActionResult {
        if (world.isClient) {
            interactor.interact(player, world, hand, InteractionTarget.Entity(entity), hitResult)
        }

        return ActionResult.PASS
    }
}
