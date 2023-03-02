package opekope2.optigui.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import opekope2.optigui.internal.TextureReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin {
    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At("TAIL"))
    private void startRidingMixin(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        TextureReplacer.setRiddenEntity(entity);
    }

    @Inject(method = "dismountVehicle()V", at = @At("TAIL"))
    private void dismountVehicleMixin(CallbackInfo ci) {
        TextureReplacer.setRiddenEntity(null);
    }
}
