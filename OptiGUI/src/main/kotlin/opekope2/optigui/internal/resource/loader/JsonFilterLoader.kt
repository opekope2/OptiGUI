package opekope2.optigui.internal.resource.loader

import com.google.gson.JsonParseException
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.NbtCompound
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.exception.NbtFilterParseException
import opekope2.optigui.filter.TextureChangerFilter
import opekope2.optigui.resource.format.json.ILoadTimeNbtSupplier
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.OPTIGUI_JSON_RESOURCES_ROOT
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal object JsonFilterLoader : AbstractResourceLoader(Identifier.of(MOD_ID, "json_loader")) {
    override val logger: Logger = LoggerFactory.getLogger("OptiGUI/JsonFilterLoader")
    override val finder: ResourceFinder = ResourceFinder(OPTIGUI_JSON_RESOURCES_ROOT, ".json")

    private lateinit var loadTimeNbt: NbtCompound

    override fun prepare(
        manager: ResourceManager,
        profiler: Profiler
    ): List<TextureChangerFilter> {
        loadTimeNbt = NbtCompound()
        for ((key, supplier) in ILoadTimeNbtSupplier.Registry) {
            loadTimeNbt.put(key, supplier.get())
        }

        return super.prepare(manager, profiler)
    }

    override fun loadFilters(
        resourceId: Identifier,
        resource: Resource,
        manager: ResourceManager
    ): Collection<TextureChangerFilter> {
        val json = resource.reader.use {
            JsonHelper.deserialize(it, true)
        }
        val filter = JsonFilterResource.Companion.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(::JsonParseException)
        return if (!filter.testLoadFilter(loadTimeNbt).getOrThrow(::NbtFilterParseException)) listOf()
        else filter.createTextureChangerFilters(resourceId).getOrThrow(::NbtFilterParseException)
    }
}
