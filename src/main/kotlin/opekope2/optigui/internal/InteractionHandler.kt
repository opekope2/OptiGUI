package opekope2.optigui.internal

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.interaction.blockEntityFactories
import opekope2.optigui.interaction.canReplaceTexture
import opekope2.optigui.interaction.entityFactories

internal object InteractionHandler :
    UseBlockCallback, UseEntityCallback, ClientTickEvents.EndWorldTick, ClientPlayConnectionEvents.Disconnect {
    internal var filter: Filter<Interaction, Identifier> = object : Filter<Interaction, Identifier>() {
        override fun test(value: Interaction) = FilterResult<Identifier>(skip = true)
    }

    private var currentScreen: HandledScreen<*>? = null

    private var lastBlockEntity: BlockEntity? = null
    private var lastEntity: Entity? = null
    private var interactionData: Any? = null

    @JvmStatic
    var riddenEntity: Entity? = null

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        // Don't bother replacing GUI texture if it's not open
        val screen = currentScreen ?: return texture

        if (!canReplaceTexture(texture)) return texture

        return filter.test(Interaction(texture, screen.title, interactionData))
            .let { if (!it.skip && it.match) it.result else null } ?: texture
    }

    @JvmStatic
    fun handleScreenChange(screen: Screen?) {
        (screen as HandledScreen<*>?).let {
            currentScreen = it

            if (it == null) {
                lastBlockEntity = null
                lastEntity = null
            }
        }
    }

    override fun interact(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
        if (world.isClient) {
            lastBlockEntity = world.getBlockEntity(hitResult.blockPos)
            lastEntity = null
        }

        return ActionResult.PASS
    }

    override fun interact(
        player: PlayerEntity, world: World, hand: Hand, entity: Entity, hitResult: EntityHitResult?
    ): ActionResult {
        if (world.isClient) {
            lastBlockEntity = null
            lastEntity = entity
        }

        return ActionResult.PASS
    }

    override fun onEndTick(world: ClientWorld) {
        interactionData = lastBlockEntity?.let { blockEntityFactories[it.javaClass]?.createInteractionData(it) }
            ?: lastEntity?.let { entityFactories[it.javaClass]?.createInteractionData(it) }
                    ?: riddenEntity?.let { entityFactories[it.javaClass]?.createInteractionData(it) }
    }

    override fun onPlayDisconnect(handler: ClientPlayNetworkHandler?, client: MinecraftClient?) {
        // Clean up, don't let memory leak. Just to be sure.
        lastBlockEntity = null
        lastEntity = null
        riddenEntity = null
    }
}
