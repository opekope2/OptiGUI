package opekope2.optigui.internal.interaction

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.*
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
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.properties.BookProperties
import opekope2.optigui.properties.DefaultProperties
import opekope2.optigui.service.InteractionService
import opekope2.optigui.util.TexturePath
import opekope2.optigui.util.getBiomeId
import opekope2.optigui.util.identifier

internal object InteractionHandler : UseBlockCallback, UseEntityCallback, UseItemCallback {
    private val interactor: InteractionService = TextureReplacer // TODO

    init {
        UseBlockCallback.EVENT.register(this)
        UseEntityCallback.EVENT.register(this)
        UseItemCallback.EVENT.register(this)
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        if (world.isClient) {
            val blockEntity = world.getBlockEntity(hitResult.blockPos)
            val target =
                if (blockEntity != null) InteractionTarget.BlockEntity(blockEntity)
                else getBlockInteractionTarget(world, hitResult.blockPos)

            if (target != null) interactor.interact(player, world, hand, target, hitResult)
        }

        return ActionResult.PASS
    }

    private fun getBlockInteractionTarget(world: World, target: BlockPos): InteractionTarget? {
        val container = world.getBlockState(target).block.identifier
        if (TexturePath.ofContainer(container) == null) {
            // Unknown/modded container
            return null
        }

        return InteractionTarget.Preprocessed(
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
            interactor.interact(player, world, hand, InteractionTarget.Entity(entity), hitResult)
        }

        return ActionResult.PASS
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        val stack = player.getStackInHand(hand)
        val result = TypedActionResult.pass(stack)

        if (!world.isClient) return result

        val target = when (stack.item) {
            Items.WRITABLE_BOOK -> InteractionTarget.Computed {
                val currentScreen = MinecraftClient.getInstance().currentScreen
                BookProperties(
                    container = Identifier("writable_book"),
                    name = stack.name.string,
                    biome = world.getBiomeId(player.blockPos),
                    height = player.blockY,
                    currentPage = (currentScreen as? BookEditScreen)?.currentPage?.plus(1) ?: return@Computed null,
                    pageCount = (currentScreen as? BookEditScreen)?.countPages() ?: return@Computed null
                )
            }

            Items.WRITTEN_BOOK -> InteractionTarget.Computed {
                val currentScreen = MinecraftClient.getInstance().currentScreen
                BookProperties(
                    container = Identifier("written_book"),
                    name = stack.name.string,
                    biome = world.getBiomeId(player.blockPos),
                    height = player.blockY,
                    currentPage = (currentScreen as? BookScreen)?.pageIndex?.plus(1) ?: return@Computed null,
                    pageCount = (currentScreen as? BookScreen)?.pageCount ?: return@Computed null
                )
            }

            else -> return result
        }

        interactor.interact(player, world, Hand.MAIN_HAND, target, null)

        return result
    }

    @JvmStatic
    fun interact(player: PlayerEntity, world: World, currentScreen: Screen) {
        fun getHangingSignBlockId(sign: SignBlockEntity) = world.getBlockState(sign.pos).block.identifier

        val container = when (currentScreen) {
            is AbstractInventoryScreen<*> -> Identifier("player")
            is HangingSignEditScreen -> getHangingSignBlockId((currentScreen as AbstractSignEditScreen).blockEntity)
            else -> return
        }

        interactor.interact(
            player,
            world,
            Hand.MAIN_HAND,
            InteractionTarget.Computed {
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
