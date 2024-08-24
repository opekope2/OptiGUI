package opekope2.optigui.internal.interaction

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.optigui.interaction.IBeforeInteractionBeginCallback
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.registry.ContainerDefaultGuiTextureRegistry
import opekope2.optigui.util.identifier
import opekope2.optigui.util.interactionData
import opekope2.optigui.util.invalidateCachedReplacement

internal object InteractionHandler : ClientModInitializer, UseBlockCallback, UseEntityCallback, UseItemCallback,
    IBeforeInteractionBeginCallback {
    override fun onInitializeClient() {
        UseBlockCallback.EVENT.register(this)
        UseEntityCallback.EVENT.register(this)
        UseItemCallback.EVENT.register(this)
        IBeforeInteractionBeginCallback.EVENT.register(this)
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        if (!world.isClient) return ActionResult.PASS

        val container = world.getBlockState(hitResult.blockPos).block.identifier
        val blockEntity = world.getBlockEntity(hitResult.blockPos)

        if (blockEntity != null) {
            Interaction.prepare(container, player, world, hand, hitResult, null, blockEntity)
            return ActionResult.PASS
        }

        if (container in ContainerDefaultGuiTextureRegistry) {
            Interaction.prepare(container, player, world, hand, hitResult, null)
        }

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

        val container = entity.identifier
        Interaction.prepare(container, player, world, hand, hitResult, null, entity)

        return ActionResult.PASS
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        val stack = player.getStackInHand(hand)
        val result = TypedActionResult.pass(stack)

        if (!world.isClient) return result

        if (stack.isOf(Items.WRITABLE_BOOK) || stack.isOf(Items.WRITTEN_BOOK)) {
            Interaction.prepare(stack.item.identifier, player, world, Hand.MAIN_HAND, null, BookExtraProperties(0, 0))
            // BookExtraProperties will be updated later
        }
        return result
    }

    override fun onBeforeBegin(screen: Screen) {
        when (screen) {
            is BookEditScreen -> tryUpdateBookProperties(screen.currentPage + 1, screen.countPages())
            is BookScreen -> tryUpdateBookProperties(screen.pageIndex + 1, screen.pageCount)
        }
    }

    @JvmStatic
    fun interact(player: PlayerEntity, world: World, currentScreen: Screen) {
        val container = when (currentScreen) {
            is AbstractInventoryScreen<*> -> Identifier.ofVanilla("player")
            is HangingSignEditScreen -> world.getBlockState(currentScreen.blockEntity.pos).block.identifier
            else -> return
        }

        Interaction.prepare(container, player, world, Hand.MAIN_HAND, null, null)
    }

    @JvmStatic
    fun tryUpdateBookProperties(currentPage: Int, pageCount: Int) {
        (interactionData?.extra as? BookExtraProperties)?.let {
            it.currentPage = currentPage
            it.pageCount = pageCount
            invalidateCachedReplacement()
        }
    }
}
