package opekope2.optigui.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import opekope2.optigui.interaction.InteractionManager;
import opekope2.optigui.interaction.data.EntityInteractionData;
import opekope2.optigui.interaction.data.InteractionPlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "openRidingInventory", at = @At("HEAD"))
    void prepareInteractionFromVehicle(CallbackInfo ci) {
        Entity vehicle = getVehicle();

        if (vehicle instanceof RideableInventory) {
            InteractionManager.prepare(
                    new EntityInteractionData(
                            vehicle,
                            getMainHandStack(),
                            new InteractionPlayerData(this, Hand.MAIN_HAND)
                    )
            );
        }
    }
}
