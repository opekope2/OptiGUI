package opekope2.optigui.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import opekope2.optigui.internal.TextureReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = DrawContext.class, priority = 900)
abstract class DrawContextMixin {
    @ModifyVariable(method = "drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V", at = @At("HEAD"), index = 2, argsOnly = true)
    private Identifier drawTextureMixin(Identifier id) {
        return TextureReplacer.isReplacingTextures() && id != null ? TextureReplacer.replaceTexture(id) : id;
    }
}
