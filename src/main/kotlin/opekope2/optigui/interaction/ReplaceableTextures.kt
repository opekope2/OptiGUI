package opekope2.optigui.interaction

import net.minecraft.util.Identifier


private val replaceableTextures = mutableSetOf<Identifier>()

/**
 * Adds a texture to the set of textures, which may be replaced.
 *
 * @param texturePath The replaceable texture.
 */
fun registerReplaceableTexture(texturePath: Identifier) {
    replaceableTextures.add(texturePath)
}

/**
 * Returns if the selected texture is registered to be replaced using [registerReplaceableTexture].
 *
 * @param texturePath The texture to query
 */
fun canReplaceTexture(texturePath: Identifier) = texturePath in replaceableTextures
