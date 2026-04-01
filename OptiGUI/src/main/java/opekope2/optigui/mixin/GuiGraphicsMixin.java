package opekope2.optigui.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import opekope2.optigui.internal.TextureReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GuiGraphics.class, priority = 900)
abstract class GuiGraphicsMixin {
    @ModifyVariable(method = "blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIIII)V", at = @At("HEAD"), index = 2, argsOnly = true)
    private ResourceLocation drawTextureMixin(ResourceLocation id) {
        return TextureReplacer.isReplacingTextures() && id != null ? TextureReplacer.replaceTexture(id) : id;
    }
}
