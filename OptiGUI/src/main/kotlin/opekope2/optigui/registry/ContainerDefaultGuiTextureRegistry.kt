@file: JvmName("ContainerTexturePath")

package opekope2.optigui.registry

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModMetadata
import net.minecraft.util.Identifier

/**
 * Texture paths for Minecraft containers.
 *
 * To register your mod's containers' default GUI textures, create a
 * [custom field](https://fabricmc.net/wiki/documentation:fabric_mod_json#custom_fields) named
 * `optigui:container_default_textures`. It must be a JSON object. They keys are the container IDs, and the values are
 * the default GUI textures.
 */
object ContainerDefaultGuiTextureRegistry : ClientModInitializer {
    private const val CONTAINER_DEFAULT_TEXTURES_KEY = "optigui:container_default_gui_textures"

    private lateinit var containerTextures: MutableMap<Identifier, Identifier>

    override fun onInitializeClient() {
        containerTextures = mutableMapOf()
        for (mod in FabricLoader.getInstance().allMods) {
            try {
                loadModTextures(mod.metadata)
            } catch (e: Exception) {
                throw RuntimeException("Failed to load container default GUI textures from mod `${mod.metadata.id}`", e)
            }
        }
    }

    private fun loadModTextures(metadata: ModMetadata) {
        if (!metadata.containsCustomValue(CONTAINER_DEFAULT_TEXTURES_KEY)) return
        val textures = metadata.getCustomValue(CONTAINER_DEFAULT_TEXTURES_KEY)
        val texturesObj = textures.asObject

        for ((key, value) in texturesObj) {
            val valueString = value.asString

            val keyId = Identifier.of(key)
            val valueId = Identifier.of(valueString)

            if (keyId in containerTextures) {
                throw RuntimeException("A default GUI texture for container `$key` has already been registered")
            }

            containerTextures[keyId] = valueId
        }
    }

    /**
     * Checks if the default GUI texture of a container is known.
     *
     * @param container The id of the entity or block entity
     */
    @JvmStatic
    operator fun contains(container: Identifier) = container in containerTextures

    /**
     * Gets the default GUI texture of the given container, or `null`, if it's unknown.
     *
     * @param container The id of the entity or block entity to get its GUI texture
     */
    @JvmStatic
    operator fun get(container: Identifier) = containerTextures[container]
}
