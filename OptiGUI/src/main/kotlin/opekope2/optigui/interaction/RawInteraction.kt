package opekope2.optigui.interaction

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import opekope2.optigui.service.InteractionService

/**
 * Raw interaction details. When using preprocessors, this is likely unnecessary.
 * Only required, when entities and block entities are unavailable (for example, anvils, and many villager job sites).
 * Unavailable for ridden entities.
 *
 * @see UseBlockCallback.interact
 * @see UseEntityCallback.interact
 * @see InteractionService.interact
 */
data class RawInteraction(val player: PlayerEntity, val world: World, val hand: Hand, val hitResult: HitResult?)
