package opekope2.optigui.util

import com.google.common.hash.Hashing
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.texture.TextureManager
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.InputStream

/**
 * Utility that loads and manages resource pack icons.
 *
 * @param name The name of this resource pack icon loader. This is associated with the loaded icons
 * @param textureManager [MinecraftClient.getTextureManager]
 */
class ResourcePackIconLoader(name: String, private val textureManager: TextureManager) : Closeable {
    private val name = Util.replaceInvalidChars(name, Identifier::isPathCharacterValid)
    private val logger = LoggerFactory.getLogger(javaClass)
    private val textures = mutableListOf<Identifier>()
    private val hasher = Hashing.goodFastHash(128)

    /**
     * Loads the icon of the given resource pack profile.
     *
     * @param profile The resource pack profile to load the icon from
     * @return The [Identifier] of the loaded icon, which can be used until this resource pack icon loader is
     *   [closed][close]
     */
    fun loadIcon(profile: ResourcePackProfile) = loadIcon(profile.createResourcePack())

    /**
     * Loads the icon of the given resource pack.
     *
     * @param pack The resource pack to load the icon from
     * @return The [Identifier] of the loaded icon, which can be used until this resource pack icon loader is
     *   [closed][close]
     */
    fun loadIcon(pack: ResourcePack): Identifier {
        val inputSupplier = pack.openRoot("pack.png") ?: return UNKNOWN_PACK
        return tryLoadIcon(pack.id, inputSupplier.get()) ?: UNKNOWN_PACK
    }

    /**
     * Tries to load an icon from the given stream.
     *
     * @param packName The name of the resource pack. Used to identify the loaded image
     * @param stream The stream to read the icon from
     * @return The [Identifier] of the loaded icon, which can be used until this resource pack icon loader is
     *   [closed][close] or `null`, if an exception occurs trying to load the icon
     */
    fun tryLoadIcon(packName: String, stream: InputStream): Identifier? = try {
        val validPackName = Util.replaceInvalidChars(packName, Identifier::isPathCharacterValid)
        val packNameHash = hasher.hashUnencodedChars(packName).toString()
        val packIconIdPath = "resource_pack/$name/$validPackName/$packNameHash/icon.png"
        val packIconId = Identifier.of(MOD_ID, packIconIdPath)

        if (packIconId !in textures) {
            val image = stream.use(NativeImage::read)
            textureManager.registerTexture(packIconId, NativeImageBackedTexture(image))
            textures += packIconId
        }

        packIconId
    } catch (e: Exception) {
        logger.error("Error loading icon from resource pack '{}'", packName, e)
        null
    }

    /**
     * Unloads all loaded resource pack icons.
     */
    override fun close() {
        // TextureManager::destroy calls NativeImageBackedTexture::close
        textures.forEach(textureManager::destroyTexture)
    }

    companion object {
        /**
         * The texture ID of a resource pack without a `pack.png` file.
         */
        @JvmField
        val UNKNOWN_PACK: Identifier = Identifier.ofVanilla("textures/misc/unknown_pack.png")
    }
}
