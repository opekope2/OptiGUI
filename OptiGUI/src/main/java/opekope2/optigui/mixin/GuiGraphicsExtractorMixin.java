package opekope2.optigui.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import opekope2.optigui.internal.TextureReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GuiGraphicsExtractor.class, priority = 900)
abstract class GuiGraphicsExtractorMixin {
    @ModifyVariable(method = "blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V", at = @At("HEAD"), index = 2, argsOnly = true)
    private Identifier drawTextureMixin(Identifier id) {
        return TextureReplacer.isReplacingTextures() && id != null ? TextureReplacer.replaceTexture(id) : id;
    }
}
