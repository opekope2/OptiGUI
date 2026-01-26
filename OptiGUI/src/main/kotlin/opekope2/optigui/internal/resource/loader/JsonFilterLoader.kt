package opekope2.optigui.internal.resource.loader

import dev.runefox.json.Json
import dev.runefox.json.JsonNode
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.LOG_KEY_RESOURCE
import opekope2.optigui.util.LOG_KEY_RESOURCE_PACK
import opekope2.optigui.util.OPTIGUI_JSON_RESOURCES_ROOT
import opekope2.optigui.util.dfu.Json5Ops

internal object JsonFilterLoader : AbstractResourceLoader<JsonNode>("json_loader") {
    private val json = Json.json5()

    override fun findResources(manager: ResourceManager) =
        manager.listResources(OPTIGUI_JSON_RESOURCES_ROOT) { it.path.endsWith(".json5") || it.path.endsWith(".json") }

    override fun loadResource(
        resource: IdentifiableResource<Resource>,
        manager: ResourceManager,
        collector: ResourceCollector
    ) {
        logger.atDebug()
            .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.resource.sourcePackId())
            .addKeyValue(LOG_KEY_RESOURCE, resource.id)
            .log("Loading resource {}", resource.id)

        val json = resource.resource.openAsReader().use(json::parse)
        val result = JsonFilterResource.CODEC.parse(Json5Ops, json).map(resource::withResource)
        collector.addResource(result)

        if (result.isSuccess) logger.atDebug()
            .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
            .addKeyValue(LOG_KEY_RESOURCE, resource.id)
            .log("Loaded resource {}", resource.id)
    }
}
