package opekope2.optigui.filter.texture_changer

import net.minecraft.resources.ResourceLocation

/**
 * An [ITextureChanger], which changes textures to the one specified in the constructor.
 *
 * @param newTexture The new texture
 */
class SimpleTextureChanger(val newTexture: ResourceLocation) : ITextureChanger {
    override fun apply(texture: ResourceLocation) = newTexture
}
