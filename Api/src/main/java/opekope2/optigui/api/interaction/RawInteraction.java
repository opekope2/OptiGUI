package opekope2.optigui.api.interaction;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Raw interaction details. When using processors, this is likely unnecessary.
 * Only required, when entities and block entities are unavailable (for example, anvils, and many villager job sites).
 * Unavailable for ridden entities.
 *
 * @see UseBlockCallback#interact(PlayerEntity, World, Hand, BlockHitResult)
 * @see UseEntityCallback#interact(PlayerEntity, World, Hand, Entity, EntityHitResult)
 * @see IInteractor#interact(PlayerEntity, World, Hand, IInteractionTarget, HitResult)
 */
public record RawInteraction(
        @NotNull PlayerEntity player,
        @NotNull World world,
        @NotNull Hand hand,
        @Nullable HitResult hitResult
) {
}
