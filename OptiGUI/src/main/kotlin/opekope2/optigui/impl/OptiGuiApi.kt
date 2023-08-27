package opekope2.optigui.impl

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.entity.BlockEntity
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
import opekope2.util.TreeFormatter
import org.slf4j.LoggerFactory

object OptiGuiApi : IOptiGuiApi, ClientModInitializer {
    private val logger = LoggerFactory.getLogger("OptiGUI/MetadataLoader")
    private lateinit var containerTextureMap: Map<Identifier, Identifier>

    override fun isAvailable(): Boolean = true

    override fun getImplementationModId(): String = "optigui"

    override fun getInteractor(): IInteractor = TextureReplacer

    @Suppress("UNCHECKED_CAST")
    override fun <T : Entity> getEntityProcessor(type: Class<T>): IEntityProcessor<T>? {
        return entityProcessors[type] as IEntityProcessor<T>?
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity> getBlockEntityProcessor(type: Class<T>): IBlockEntityProcessor<T>? {
        return blockEntityProcessors[type] as IBlockEntityProcessor<T>?
    }

    override fun onInitializeClient() {
        if (this::containerTextureMap.isInitialized) {
            logger.info("Not initializing container texture mapping, because it is already initialized")
            return
        }

        val metadata = FabricLoader.getInstance().allMods.mapNotNull { mod -> mod.metadata.getCustomValue("optigui") }
        val serializer = ILilacApi.getImplementation().customMetadataSerializer
        val exception = SetOnce<Exception>()
        val parsed = metadata.mapNotNull { customValue ->
            try {
                serializer.deserialize(ProcessableCustomMetadata.CODEC, customValue).process()
            } catch (e: Exception) {
                if (exception.isSet()) exception.value.addSuppressed(e)
                else exception.set(RuntimeException("Failed to parse `fabric.mod.json` metadata", e))
                null
            }
        }

        if (exception.isSet()) throw exception.value

        val containerTextureMap = mutableMapOf<Identifier, Identifier>()
        val conflicts = mutableMapOf<Identifier, MutableSet<Identifier>>()

        for (meta in parsed) {
            for ((container, texture) in meta.containerTextures) {
                if (container in containerTextureMap) {
                    conflicts.getOrPut(container) { mutableSetOf(containerTextureMap[container]!!) }.add(texture)
                } else {
                    containerTextureMap[container] = texture
                }
            }
        }

        if (conflicts.isNotEmpty()) {
            val conflictTree = dumpConflicts(conflicts)

            logger.error("Multiple textures were found for one or more container:\n$conflictTree")
            throw RuntimeException(
                "Multiple textures were found for one or more container. This is not an OptiGUI bug. Some of the mods are incompatible with each other. See log for details"
            )
        }

        this.containerTextureMap = containerTextureMap
    }

    private fun dumpConflicts(conflicts: Map<Identifier, MutableSet<*>>): String {
        val writer = TreeFormatter()

        writer.indent {
            append("Container texture conflicts", lastChild = true)

            dumpConflicts(conflicts.asIterable(), this)
        }

        return writer.toString()

    }

    @Suppress("UNCHECKED_CAST")
    private fun dumpConflicts(iterable: Iterable<*>, writer: TreeFormatter) {
        writer.indent {
            val it = iterable.iterator()
            while (it.hasNext()) {
                val next = it.next()
                val (id, set) = next as Map.Entry<Identifier, MutableSet<*>>
                append(id.toString(), !it.hasNext())

                dumpConflicts(set, writer)
            }
        }
    }

    private class SetOnce<T : Any> {
        lateinit var value: T
            private set

        fun isSet() = ::value.isInitialized

        fun set(value: T) {
            if (isSet()) throw IllegalStateException()
            this.value = value
        }
    }
}
