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
import opekope2.lilac.api.registry.IRegistryLookup
import opekope2.optigui.api.interaction.IInteractionTarget
import opekope2.optigui.api.interaction.IInteractor
import opekope2.optigui.mixin.IHangingSignEditScreenMixin
import opekope2.optigui.properties.impl.BookProperties
import opekope2.optigui.properties.impl.CommonProperties
import opekope2.optigui.properties.impl.GeneralProperties
import opekope2.optigui.properties.impl.IndependentProperties
import opekope2.util.TexturePath
import java.time.LocalDate

internal object InteractionHandler : ClientModInitializer, UseBlockCallback, UseEntityCallback, UseItemCallback {
    private val interactor = IInteractor.getInstance()
    private val lookup = IRegistryLookup.getInstance()

    override fun onInitializeClient() {
        UseBlockCallback.EVENT.register(this)
        UseEntityCallback.EVENT.register(this)
        UseItemCallback.EVENT.register(this)
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        if (world.isClient) {
            val blockEntity = world.getBlockEntity(hitResult.blockPos)
            val target =
                if (blockEntity?.hasProcessor == true) IInteractionTarget.BlockEntityTarget(blockEntity)
                else getBlockInteractionTarget(world, hitResult.blockPos)

            if (target != null) interactor.interact(player, world, hand, target, hitResult)
        }

        return ActionResult.PASS
    }

    private fun getBlockInteractionTarget(world: World, target: BlockPos): IInteractionTarget? {
        val container = lookup.lookupBlockId(world.getBlockState(target).block)
        if (TexturePath.ofContainer(container) == null) {
            // Unknown/modded container
            return null
        }

        return IInteractionTarget.ComputedTarget(
            CommonProperties(
                GeneralProperties(
                    container = container,
                    name = null,
                    biome = lookup.lookupBiomeId(world, target),
                    height = target.y
                ),
                IndependentProperties(
                    date = LocalDate.now()
                )
            )
        )
    }

    override fun interact(
        player: PlayerEntity, world: World, hand: Hand, entity: Entity, hitResult: EntityHitResult?
    ): ActionResult {
        if (world.isClient && entity.hasProcessor) {
            interactor.interact(player, world, hand, IInteractionTarget.EntityTarget(entity), hitResult)
        }

        return ActionResult.PASS
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        val stack = player.getStackInHand(hand)
        val result = TypedActionResult.pass(stack)

        if (!world.isClient) return result

        val target = when (stack.item) {
            Items.WRITABLE_BOOK -> IInteractionTarget.ComputedTarget {
                val bookScreen = MinecraftClient.getInstance().currentScreen as? BookEditScreen
                    ?: return@ComputedTarget null

                BookProperties(
                    CommonProperties(
                        GeneralProperties(
                            container = Identifier("writable_book"),
                            name = stack.name.string,
                            biome = lookup.lookupBiomeId(world, player.blockPos),
                            height = player.blockY
                        ),
                        IndependentProperties(
                            date = LocalDate.now()
                        )
                    ),
                    currentPage = bookScreen.currentPage + 1,
                    pageCount = bookScreen.countPages()
                )
            }

            Items.WRITTEN_BOOK -> IInteractionTarget.ComputedTarget {
                val bookScreen = MinecraftClient.getInstance().currentScreen as? BookScreen
                    ?: return@ComputedTarget null

                BookProperties(
                    CommonProperties(
                        GeneralProperties(
                            container = Identifier("written_book"),
                            name = stack.name.string,
                            biome = lookup.lookupBiomeId(world, player.blockPos),
                            height = player.blockY
                        ),
                        IndependentProperties(
                            date = LocalDate.now()
                        )
                    ),
                    currentPage = bookScreen.pageIndex + 1,
                    pageCount = bookScreen.pageCount
                )
            }

            else -> return result
        }

        interactor.interact(player, world, Hand.MAIN_HAND, target, null)

        return result
    }

    @JvmStatic
    fun interact(player: PlayerEntity, world: World, currentScreen: Screen) {
        val container = when (currentScreen) {
            is AbstractInventoryScreen<*> -> Identifier("player")
            is IHangingSignEditScreenMixin -> lookup.lookupBlockId(world.getBlockState(currentScreen.blockEntity.pos).block)
            else -> return
        }

        interactor.interact(
            player,
            world,
            Hand.MAIN_HAND,
            IInteractionTarget.ComputedTarget {
                CommonProperties(
                    GeneralProperties(
                        container = container,
                        name = player.name.string,
                        biome = lookup.lookupBiomeId(world, player.blockPos),
                        height = player.blockY
                    ),
                    IndependentProperties(
                        date = LocalDate.now()
                    )
                )
            },
            null
        )
    }
}
