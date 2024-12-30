package opekope2.optigui.internal.interaction

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.data.BlockInteractionData
import opekope2.optigui.interaction.data.EntityInteractionData
import opekope2.optigui.interaction.data.InteractionPlayerData
import opekope2.optigui.interaction.data.ItemInteractionData

internal object InteractionHandler : ClientModInitializer, UseBlockCallback, UseEntityCallback, UseItemCallback {
    override fun onInitializeClient() {
        UseBlockCallback.EVENT.register(this)
        UseEntityCallback.EVENT.register(this)
        UseItemCallback.EVENT.register(this)
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        if (!world.isClient) return ActionResult.PASS

        val blockPos = hitResult.blockPos
        val blockState = world.getBlockState(blockPos)
        val blockEntity = world.getBlockEntity(blockPos)

        InteractionManager.prepare(
            BlockInteractionData(
                blockPos,
                blockState,
                blockEntity,
                player.getStackInHand(hand),
                InteractionPlayerData(player, hand)
            )
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

        InteractionManager.prepare(
            EntityInteractionData(entity, player.getStackInHand(hand), InteractionPlayerData(player, hand))
        )

        return ActionResult.PASS
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        val stack = player.getStackInHand(hand)
        val result = TypedActionResult.pass(stack)

        if (!world.isClient) return result

        if (stack.isOf(Items.WRITABLE_BOOK) || stack.isOf(Items.WRITTEN_BOOK)) {
            InteractionManager.prepare(
                ItemInteractionData(stack, InteractionPlayerData(player, hand))
            )
        }
        return result
    }
}
