package opekope2.optigui.internal.resource.loader

import dev.runefox.json.Json
import dev.runefox.json.JsonNode
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.OPTIGUI_JSON_RESOURCES_ROOT
import opekope2.optigui.util.dfu.Json5Ops

internal object JsonFilterLoader : AbstractResourceLoader<JsonNode>("json_loader") {
    private val json = Json.json5()

    override fun findResources(manager: ResourceManager) =
        manager.listResources(OPTIGUI_JSON_RESOURCES_ROOT) { it.path.endsWith(".json5") || it.path.endsWith(".json") }

    override fun loadResource(
        packId: String,
        resourceId: ResourceLocation,
        resource: Resource,
        manager: ResourceManager
    ): JsonNode = resource.openAsReader().use(json::parse)

    override fun parseResource(resource: IdentifiableResource<JsonNode>, collector: ResourceCollector) {
        val json = JsonFilterResource.CODEC.parse(Json5Ops, resource.resource).map(resource::withResource)
        collector.addResource(json)
    }
}
