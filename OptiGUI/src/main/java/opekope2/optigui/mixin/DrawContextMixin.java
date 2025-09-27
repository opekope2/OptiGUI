package opekope2.optigui.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import opekope2.optigui.internal.TextureChanger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(value = DrawContext.class, priority = 800)
abstract class DrawContextMixin {
    @ModifyVariable(
            method = "drawTexture(Lnet/minecraft/util/Identifier;IIIIIIIFFII)V",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true
    )
    private Identifier changeTexture(@Nullable Identifier texture) {
        return texture != null ? TextureChanger.changeTexture(texture) : null;
    }

    @ModifyVariable(
            method = {
                    "drawGuiTexture(Lnet/minecraft/util/Identifier;IIIII)V",
                    "drawGuiTexture(Lnet/minecraft/util/Identifier;IIIIIIIII)V"
            },
            at = @At("HEAD"),
            index = 1,
            argsOnly = true
    )
    private Identifier changeSprite(@Nullable Identifier sprite) {
        return sprite != null ? TextureChanger.changeSprite(sprite) : null;
    }
}
