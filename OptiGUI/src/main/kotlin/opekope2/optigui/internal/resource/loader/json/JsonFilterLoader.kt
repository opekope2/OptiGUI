package opekope2.optigui.internal.resource.loader.json

import com.google.gson.JsonParseException
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.NbtCompound
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SinglePreparationResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.TextureChangerFilter
import opekope2.optigui.resource.format.json.ILoadTimeNbtSupplier
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.OPTIGUI_JSON_RESOURCES_ROOT
import opekope2.optigui.util.push
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException

private typealias TextureChangerFilterList = List<TextureChangerFilter>

internal object JsonFilterLoader : SinglePreparationResourceReloader<TextureChangerFilterList>(), IFilterLoader {
    val ID = Identifier.of(MOD_ID, "json_loader")!!
    private val LOGGER = LoggerFactory.getLogger("OptiGUI/JsonFilterLoader")
    private val FINDER = ResourceFinder(OPTIGUI_JSON_RESOURCES_ROOT, ".json")

    private lateinit var filters: TextureChangerFilterList

    init {
        IFilterLoader.register(ID, this)
    }

    override fun prepare(manager: ResourceManager, profiler: Profiler) = profiler.push("json_load") {
        val loadTimeNbt = NbtCompound()

        for ((key, supplier) in ILoadTimeNbtSupplier) {
            loadTimeNbt.put(key, supplier.get())
        }

        FINDER.findResources(manager).flatMap { (id, resource) ->
            try {
                val json = resource.reader.use {
                    JsonHelper.deserialize(it, true)
                }
                val filter = JsonFilterResource.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(::JsonParseException)
                if (!filter.testLoadFilter(loadTimeNbt).getOrThrow(::JsonParseException)) return@flatMap listOf()

                val filters = filter.createTextureChangerFilters().getOrThrow(::JsonParseException)
                val missingTextures = filters.flatMapTo(mutableSetOf()) { it.textureChanges.values }
                    .filter { manager.getResource(it).isEmpty }
                if (missingTextures.isNotEmpty()) throw FileNotFoundException("Missing textures: ${missingTextures.joinToString()}")

                filters
            } catch (e: Exception) {
                LOGGER.error("Error loading resource {}", id, e)
                listOf()
            }
        }
    }

    override fun apply(prepared: TextureChangerFilterList, manager: ResourceManager, profiler: Profiler) {
        filters = prepared
    }

    override fun get() = filters
}
