package opekope2.optigui.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import opekope2.optigui.interaction.EntityInteraction;
import opekope2.optigui.interaction.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    private ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "openRidingInventory", at = @At("HEAD"))
    void prepareInteractionFromVehicle(CallbackInfo ci) {
        Entity vehicle = getVehicle();

        if (vehicle instanceof RideableInventory) {
            InteractionManager.prepare(EntityInteraction.factory(vehicle, this, Hand.MAIN_HAND));
        }
    }
}
