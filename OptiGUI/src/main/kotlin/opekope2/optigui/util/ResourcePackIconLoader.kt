package opekope2.optigui.util

import com.google.common.hash.Hashing
import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.repository.Pack
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.InputStream

/**
 * Utility that loads and manages resource pack icons.
 *
 * @param name The name of this resource pack icon loader. This is associated with the loaded icons
 * @param textureManager [Minecraft.getTextureManager]
 */
class ResourcePackIconLoader(name: String, private val textureManager: TextureManager) : Closeable {
    private val name = Util.sanitizeName(name, ResourceLocation::validPathChar)
    private val logger = LoggerFactory.getLogger(javaClass)
    private val textures = mutableListOf<ResourceLocation>()
    private val hasher = Hashing.goodFastHash(128)

    /**
     * Loads the icon of the given resource pack profile.
     *
     * @param profile The resource pack profile to load the icon from
     * @return The [ResourceLocation] of the loaded icon, which can be used until this resource pack icon loader is
     *   [closed][close]
     */
    fun loadIcon(profile: Pack) = loadIcon(profile.open())

    /**
     * Loads the icon of the given resource pack.
     *
     * @param pack The resource pack to load the icon from
     * @return The [ResourceLocation] of the loaded icon, which can be used until this resource pack icon loader is
     *   [closed][close]
     */
    fun loadIcon(pack: PackResources): ResourceLocation {
        val inputSupplier = pack.getRootResource("pack.png") ?: return UNKNOWN_PACK
        return tryLoadIcon(pack.packId(), inputSupplier.get()) ?: UNKNOWN_PACK
    }

    /**
     * Tries to load an icon from the given stream.
     *
     * @param packName The name of the resource pack. Used to identify the loaded image
     * @param stream The stream to read the icon from
     * @return The [ResourceLocation] of the loaded icon, which can be used until this resource pack icon loader is
     *   [closed][close] or `null`, if an exception occurs trying to load the icon
     */
    fun tryLoadIcon(packName: String, stream: InputStream) = try {
        val validPackName = Util.sanitizeName(packName, ResourceLocation::validPathChar)
        val packNameHash = hasher.hashUnencodedChars(packName).toString()
        val packIconIdPath = "resource_pack/$name/$validPackName/$packNameHash/icon.png"
        val packIconId = ResourceLocation.fromNamespaceAndPath(MOD_ID, packIconIdPath)

        if (packIconId !in textures) {
            val image = stream.use(NativeImage::read)
            textureManager.register(packIconId, DynamicTexture(image))
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
        textures.forEach(textureManager::release)
    }

    companion object {
        /**
         * The texture ID of a resource pack without a `pack.png` file.
         */
        @JvmField
        val UNKNOWN_PACK = ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png")
    }
}
