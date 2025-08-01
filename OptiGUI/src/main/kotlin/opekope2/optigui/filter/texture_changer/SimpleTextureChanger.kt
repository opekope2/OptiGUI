package opekope2.optigui.filter.texture_changer

import net.minecraft.util.Identifier

/**
 * An [ITextureChanger], which changes textures to the one specified in the constructor.
 *
 * @param newTexture The new texture
 */
class SimpleTextureChanger(val newTexture: Identifier) : ITextureChanger {
    override fun apply(texture: Identifier) = newTexture
}
