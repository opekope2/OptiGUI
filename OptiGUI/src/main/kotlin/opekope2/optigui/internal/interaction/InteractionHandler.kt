package opekope2.optigui.internal.interaction

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.MinecraftClient
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
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import opekope2.optigui.interaction.IInteractionTarget
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.properties.BookProperties
import opekope2.optigui.properties.DefaultProperties
import opekope2.optigui.util.TexturePath
import opekope2.optigui.util.getBiomeId
import opekope2.optigui.util.identifier

internal object InteractionHandler : ClientModInitializer, UseBlockCallback, UseEntityCallback, UseItemCallback {
    override fun onInitializeClient() {
        UseBlockCallback.EVENT.register(this)
        UseEntityCallback.EVENT.register(this)
        UseItemCallback.EVENT.register(this)
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        if (world.isClient) {
            val blockEntity = world.getBlockEntity(hitResult.blockPos)
            val target =
                if (blockEntity != null) IInteractionTarget.BlockEntityTarget(blockEntity)
                else getBlockInteractionTarget(world, hitResult.blockPos)

            if (target != null) Interaction.prepare(player, world, hand, target, hitResult)
        }

        return ActionResult.PASS
    }

    private fun getBlockInteractionTarget(world: World, target: BlockPos): IInteractionTarget? {
        val container = world.getBlockState(target).block.identifier
        if (TexturePath.ofContainer(container) == null) {
            // Unknown/modded container
            return null
        }

        return IInteractionTarget.ComputedTarget(
            DefaultProperties(
                container = container,
                name = null,
                biome = world.getBiomeId(target),
                height = target.y
            )
        )
    }

    override fun interact(
        player: PlayerEntity, world: World, hand: Hand, entity: Entity, hitResult: EntityHitResult?
    ): ActionResult {
        if (world.isClient) {
            Interaction.prepare(player, world, hand, IInteractionTarget.EntityTarget(entity), hitResult)
        }

        return ActionResult.PASS
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        val stack = player.getStackInHand(hand)
        val result = TypedActionResult.pass(stack)

        if (!world.isClient) return result

        val target = when (stack.item) {
            Items.WRITABLE_BOOK -> IInteractionTarget.ComputedTarget {
                val currentScreen = MinecraftClient.getInstance().currentScreen as? BookEditScreen
                    ?: return@ComputedTarget null
                BookProperties(
                    container = Identifier("writable_book"),
                    name = stack.name.string,
                    biome = world.getBiomeId(player.blockPos),
                    height = player.blockY,
                    currentPage = currentScreen.currentPage + 1,
                    pageCount = currentScreen.countPages()
                )
            }

            Items.WRITTEN_BOOK -> IInteractionTarget.ComputedTarget {
                val currentScreen = MinecraftClient.getInstance().currentScreen as? BookScreen
                    ?: return@ComputedTarget null
                BookProperties(
                    container = Identifier("written_book"),
                    name = stack.name.string,
                    biome = world.getBiomeId(player.blockPos),
                    height = player.blockY,
                    currentPage = currentScreen.pageIndex + 1,
                    pageCount = currentScreen.pageCount
                )
            }

            else -> return result
        }

        Interaction.prepare(player, world, Hand.MAIN_HAND, target, null)

        return result
    }

    @JvmStatic
    fun interact(player: PlayerEntity, world: World, currentScreen: Screen) {
        val container = when (currentScreen) {
            is AbstractInventoryScreen<*> -> Identifier("player")
            is HangingSignEditScreen -> world.getBlockState(currentScreen.blockEntity.pos).block.identifier
            else -> return
        }

        Interaction.prepare(
            player,
            world,
            Hand.MAIN_HAND,
            IInteractionTarget.ComputedTarget {
                DefaultProperties(
                    container,
                    player.name.string,
                    world.getBiomeId(player.blockPos),
                    player.blockY
                )
            },
            null
        )
    }
}
