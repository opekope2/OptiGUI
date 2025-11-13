package opekope2.optigui.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import opekope2.optigui.interaction.EntityInteraction;
import opekope2.optigui.interaction.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends Player {
    private ClientPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "sendOpenInventory", at = @At("HEAD"))
    void prepareInteractionWithVehicle(CallbackInfo ci) {
        Entity vehicle = getVehicle();

        if (vehicle instanceof HasCustomInventoryScreen) {
            InteractionManager.prepare(EntityInteraction.factory(vehicle, this, InteractionHand.MAIN_HAND));
        }
    }
}
