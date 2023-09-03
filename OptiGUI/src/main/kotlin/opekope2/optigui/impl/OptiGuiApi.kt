package opekope2.optigui.impl

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier
import opekope2.lilac.api.ILilacApi
import opekope2.optigui.api.IOptiGuiApi
import opekope2.optigui.api.interaction.IBlockEntityProcessor
import opekope2.optigui.api.interaction.IEntityProcessor
import opekope2.optigui.api.interaction.IInteractor
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.internal.fabric.mod_json.metadata.ProcessableCustomMetadata
import opekope2.optigui.internal.interaction.blockEntityProcessors
import opekope2.optigui.internal.interaction.entityProcessors
import opekope2.util.ConflictHandlingMap
import opekope2.util.IIdentifiable
import opekope2.util.isSuperOf
import org.slf4j.LoggerFactory

object OptiGuiApi : IOptiGuiApi, ClientModInitializer {
    private val logger = LoggerFactory.getLogger("OptiGUI/MetadataLoader")
    private lateinit var containerTextureMap: Map<Identifier, Identifier>
    private val retexturableScreens = mutableSetOf<Class<out Screen>>()

    override fun isAvailable(): Boolean = true

    override fun getImplementationModId(): String = "optigui"

    override fun getInteractor(): IInteractor = TextureReplacer

    override fun getContainerTexture(container: Identifier): Identifier? {
        return containerTextureMap[container]
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Entity> getEntityProcessor(type: Class<T>): IEntityProcessor<T>? {
        return entityProcessors[type] as IEntityProcessor<T>?
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity> getBlockEntityProcessor(type: Class<T>): IBlockEntityProcessor<T>? {
        return blockEntityProcessors[type] as IBlockEntityProcessor<T>?
    }

    override fun addRetexturableScreen(screenClass: Class<out Screen>) {
        retexturableScreens += screenClass
    }

    override fun isScreenRetexturable(screen: Screen): Boolean {
        return retexturableScreens.any { it.isSuperOf(screen) }
    }

    override fun onInitializeClient() {
        if (this::containerTextureMap.isInitialized) {
            logger.info("Not initializing container texture mapping, because it is already initialized")
            return
        }

        val mods = FabricLoader.getInstance().allMods
        val serializer = ILilacApi.getImplementation().customMetadataSerializer
        var exception: Exception? = null
        val parsed = mods.mapNotNull { mod ->
            val customValue = mod.metadata.getCustomValue("optigui") ?: return@mapNotNull null
            val modId = mod.metadata.id

            try {
                modId to serializer.deserialize(ProcessableCustomMetadata.CODEC, customValue).process()
            } catch (e: Exception) {
                if (exception != null) exception!!.addSuppressed(e)
                else exception = RuntimeException("Failed to parse `fabric.mod.json` metadata of `$modId`", e)
                null
            }
        }

        if (exception != null) throw exception!!

        val containerTextureMap = ConflictHandlingMap<Identifier, IdentifiableIdentifier>()

        for ((modId, meta) in parsed) {
            for ((container, texture) in meta.containerTextures) {
                containerTextureMap[container] = IdentifiableIdentifier(modId, texture)
            }
        }

        if (containerTextureMap.conflicts) {
            val conflictTree = containerTextureMap.createConflictTree("Container texture conflicts").toString()

            logger.error("Multiple textures were found for one or more container:\n$conflictTree")
            throw RuntimeException(
                "Multiple textures were found for one or more container. This is not an OptiGUI bug. Some of the mods are incompatible with each other. See log for details"
            )
        }

        this.containerTextureMap = containerTextureMap.mapValues { (_, value) -> value.identifier }
    }

    private class IdentifiableIdentifier(override val id: String, val identifier: Identifier) : IIdentifiable
}
