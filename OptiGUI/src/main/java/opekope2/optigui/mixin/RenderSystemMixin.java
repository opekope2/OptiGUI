package opekope2.optigui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import opekope2.optigui.internal.TextureReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RenderSystem.class, /* Inject before Animatica for full compatibility */ priority = 900)
abstract class RenderSystemMixin {
    @ModifyVariable(method = "_setShaderTexture(ILnet/minecraft/util/Identifier;)V", at = @At("HEAD"), index = 1, argsOnly = true)
    private static Identifier setShaderTextureMixin(Identifier id) {
        return TextureReplacer.isActive() ? TextureReplacer.replaceTexture(id) : id;
    }
}
