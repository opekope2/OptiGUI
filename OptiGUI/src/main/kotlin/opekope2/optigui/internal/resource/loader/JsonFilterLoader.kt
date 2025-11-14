package opekope2.optigui.internal.resource.loader

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.OPTIGUI_JSON_RESOURCES_ROOT

internal object JsonFilterLoader : AbstractResourceLoader<JsonObject>(Identifier.of(MOD_ID, "json_loader")) {
    private val jsonFinder = ResourceFinder.json(OPTIGUI_JSON_RESOURCES_ROOT)

    override fun findResources(manager: ResourceManager): Map<Identifier, Resource> = jsonFinder.findResources(manager)

    override fun loadResource(
        packId: String,
        resourceId: Identifier,
        resource: Resource,
        manager: ResourceManager
    ): JsonObject = resource.reader.use { JsonHelper.deserialize(it, true) }

    override fun parseResource(resource: IdentifiableResource<JsonObject>, collector: ResourceCollector) {
        val json = JsonFilterResource.CODEC.parse(JsonOps.INSTANCE, resource.resource).map(resource::withResource)
        collector.addResource(json)
    }
}
