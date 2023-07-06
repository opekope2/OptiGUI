package opekope2.optigui.internal.interaction

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.mixin.AbstractSignEditScreenAccessorMixin
import opekope2.optigui.properties.DefaultProperties
import opekope2.optigui.service.InteractionService
import opekope2.optigui.service.RegistryLookupService
import opekope2.optigui.service.getService
import opekope2.util.TexturePath

internal object InteractionHandler : UseBlockCallback, UseEntityCallback {
    private val interactor: InteractionService by lazy(::getService)
    private val lookup: RegistryLookupService by lazy(::getService)

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
        val container = lookup.lookupBlockId(world.getBlockState(target).block)
        if (TexturePath.ofContainer(container) == null) {
            // Unknown/modded container
            return null
        }

        return InteractionTarget.Preprocessed(
            DefaultProperties(
                container = container,
                name = null,
                biome = lookup.lookupBiomeId(world, target),
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

    @JvmStatic
    fun interact(player: PlayerEntity, world: World, currentScreen: Screen) {
        fun getHangingSignBlockId(sign: SignBlockEntity) = lookup.lookupBlockId(world.getBlockState(sign.pos).block)

        val container = when (currentScreen) {
            is AbstractInventoryScreen<*> -> Identifier("player")
            is HangingSignEditScreen -> getHangingSignBlockId((currentScreen as AbstractSignEditScreenAccessorMixin).blockEntity)
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
                    lookup.lookupBiomeId(world, player.blockPos),
                    player.blockY
                )
            },
            null
        )
    }
}
