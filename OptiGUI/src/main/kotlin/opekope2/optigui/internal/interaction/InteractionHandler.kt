package opekope2.optigui.internal.interaction

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.*
import net.minecraft.resources.Identifier
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import opekope2.optigui.interaction.IBeforeInteractionBeginCallback
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.registry.ContainerDefaultGuiTextureRegistry
import opekope2.optigui.util.identifier
import opekope2.optigui.util.interactionData
import opekope2.optigui.util.invalidateCachedReplacement

internal object InteractionHandler : ClientModInitializer, UseBlockCallback, UseEntityCallback, UseItemCallback,
    IBeforeInteractionBeginCallback, ScreenEvents.BeforeInit, ScreenEvents.BeforeExtract, ScreenEvents.AfterExtract {
    override fun onInitializeClient() {
        UseBlockCallback.EVENT.register(this)
        UseEntityCallback.EVENT.register(this)
        UseItemCallback.EVENT.register(this)
        IBeforeInteractionBeginCallback.EVENT.register(this)
        ScreenEvents.BEFORE_INIT.register(this)
    }

    override fun interact(player: Player, world: Level, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult {
        if (!world.isClientSide) return InteractionResult.PASS

        val container = world.getBlockState(hitResult.blockPos).block.identifier
        val blockEntity = world.getBlockEntity(hitResult.blockPos)

        if (blockEntity != null) {
            Interaction.prepare(container, player, world, hand, hitResult, null, blockEntity)
            return InteractionResult.PASS
        }

        if (container in ContainerDefaultGuiTextureRegistry) {
            Interaction.prepare(container, player, world, hand, hitResult, null)
        }

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

        val container = entity.identifier
        Interaction.prepare(container, player, world, hand, hitResult, null, entity)

        return InteractionResult.PASS
    }

    override fun interact(player: Player, world: Level, hand: InteractionHand): InteractionResult {
        if (!world.isClientSide) return InteractionResult.PASS

        val stack = player.getItemInHand(hand)
        if (stack.`is`(Items.WRITABLE_BOOK) || stack.`is`(Items.WRITTEN_BOOK)) {
            Interaction.prepare(stack.item.identifier, player, world, InteractionHand.MAIN_HAND, null, BookExtraProperties(0, 0))
            // BookExtraProperties will be updated later
        }
        return InteractionResult.PASS
    }

    override fun onBeforeBegin(screen: Screen) {
        when (screen) {
            is BookEditScreen -> tryUpdateBookProperties(screen.currentPage + 1, screen.numPages)
            is BookViewScreen -> tryUpdateBookProperties(screen.currentPage + 1, screen.numPages)
        }
    }

    override fun beforeInit(client: Minecraft, screen: Screen, scaledWidth: Int, scaledHeight: Int) {
        ScreenEvents.beforeExtract(screen).register(this)
        ScreenEvents.afterExtract(screen).register(this)
    }

    override fun beforeExtract(screen: Screen, drawContext: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, tickDelta: Float) {
        TextureReplacer.isReplacingTextures = true
    }

    override fun afterExtract(screen: Screen, drawContext: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, tickDelta: Float) {
        TextureReplacer.isReplacingTextures = false
    }

    @JvmStatic
    fun interact(player: Player, world: Level, currentScreen: Screen) {
        val container = when (currentScreen) {
            is InventoryScreen -> Identifier.withDefaultNamespace("player")
            is CreativeModeInventoryScreen -> Identifier.withDefaultNamespace("player")
            is HangingSignEditScreen -> world.getBlockState(currentScreen.sign.blockPos).block.identifier
            else -> return
        }

        Interaction.prepare(container, player, world, InteractionHand.MAIN_HAND, null, null)
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
