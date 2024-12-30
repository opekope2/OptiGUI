package opekope2.optigui.internal.resource.loader.json

import com.google.gson.JsonParseException
import com.mojang.serialization.JsonOps
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.nbt.NbtCompound
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.resource.SinglePreparationResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.TextureReplacerFilter
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.resource.load.ILoadTimeNbtSupplier
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.OPTIGUI_JSON_RESOURCES_ROOT
import opekope2.optigui.util.push
import org.slf4j.LoggerFactory

private typealias TextureReplacerFilterList = List<TextureReplacerFilter>

internal object JsonFilterLoader : SinglePreparationResourceReloader<TextureReplacerFilterList>(), IFilterLoader,
    ClientModInitializer {
    private lateinit var filters: TextureReplacerFilterList

    override fun getFabricId() = ID

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
                val filter = JsonFilterResource.PARSED_FILTER_DECODER.parse(JsonOps.INSTANCE, json)
                    .getOrThrow(::JsonParseException)

                if (filter.loadTimeFilter.test(loadTimeNbt)) filter.filters
                else listOf()
            } catch (e: Exception) {
                LOGGER.error("Error loading resource {}", id, e)
                listOf()
            }
        }
    }

    override fun apply(prepared: TextureReplacerFilterList, manager: ResourceManager, profiler: Profiler) {
        filters = prepared
    }

    override fun get() = filters

    override fun onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this)
        IFilterLoader.register(this)
    }

    private val ID = Identifier.of(MOD_ID, "json_loader")!!
    private val LOGGER = LoggerFactory.getLogger("OptiGUI/JsonFilterLoader")
    private val FINDER = ResourceFinder(OPTIGUI_JSON_RESOURCES_ROOT, ".json")
}
