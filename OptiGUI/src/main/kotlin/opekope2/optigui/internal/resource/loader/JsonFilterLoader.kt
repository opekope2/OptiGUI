package opekope2.optigui.internal.resource.loader

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.GsonHelper
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.OPTIGUI_JSON_RESOURCES_ROOT

internal object JsonFilterLoader : AbstractResourceLoader<JsonObject>("json_loader") {
    private val jsonFinder = FileToIdConverter.json(OPTIGUI_JSON_RESOURCES_ROOT)

    override fun findResources(manager: ResourceManager) = jsonFinder.listMatchingResources(manager)

    override fun loadResource(
        packId: String,
        resourceId: ResourceLocation,
        resource: Resource,
        manager: ResourceManager
    ) = resource.openAsReader().use { GsonHelper.parse(it, true) }

    override fun parseResource(resource: IdentifiableResource<JsonObject>, collector: ResourceCollector) {
        val json = JsonFilterResource.CODEC.parse(JsonOps.INSTANCE, resource.resource).map(resource::withResource)
        collector.addResource(json)
    }
}
