package opekope2.optigui.internal.resource.loader

import com.mojang.serialization.DataResult
import net.minecraft.nbt.CompoundTag
import opekope2.optigui.internal.I18n
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.LOG_KEY_RESOURCE
import opekope2.optigui.util.LOG_KEY_RESOURCE_PACK
import org.slf4j.Logger
import kotlin.jvm.optionals.getOrNull

internal class ResourceCollector(private val logger: Logger, private val loadTimeNbt: CompoundTag) :
    Iterable<IdentifiableResource<JsonFilterResource.V2>> {
    private val resources = mutableListOf<IdentifiableResource<JsonFilterResource.V2>>()

    fun <T : JsonFilterResource> addResource(resource: DataResult<IdentifiableResource<T>>) {
        resource.ifSuccess(::addResource).ifError {
            val resource = it.resultOrPartial().getOrNull()
            if (resource == null) {
                logger.atError().addArgument(it.messageSupplier).log("{}") // This will go to unknown resources
                return@ifError
            }

            val loadFilter = when (val json = resource.resource) {
                is JsonFilterResource.V1 -> json.loadFilter
                is JsonFilterResource.V2 -> json.loadFilter
                is JsonFilterResource.Future -> {
                    addFutureResource(resource.withResource(json))
                    return@ifError
                }
            }
            if (loadFilter.test(loadTimeNbt, loadTimeNbt)) logger.atError()
                .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                .addKeyValue(LOG_KEY_RESOURCE, resource.id)
                .addArgument(it.messageSupplier)
                .log("{}")
        }
    }

    fun <T : JsonFilterResource> addResource(resource: IdentifiableResource<T>) {
        when (val json = resource.resource) {
            is JsonFilterResource.V1 -> addV2Resource(resource.withResource(json.toV2()))
            is JsonFilterResource.V2 -> addV2Resource(resource.withResource(json))
            is JsonFilterResource.Future -> addFutureResource(resource.withResource(json))
        }
    }

    private fun addV2Resource(resource: IdentifiableResource<JsonFilterResource.V2>) {
        if (resource.resource.loadFilter.test(loadTimeNbt, loadTimeNbt)) {
            resources += resource
        } else {
            logger.atInfo()
                .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                .addKeyValue(LOG_KEY_RESOURCE, resource)
                .addArgument(I18n.OPTIGUI_RP_LOADER_INFO_LOAD_TIME_FILTERED.supplyTranslation())
                .log("{}")
        }
    }

    private fun addFutureResource(resource: IdentifiableResource<JsonFilterResource.Future>) {
        logger.atWarn()
            .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
            .addKeyValue(LOG_KEY_RESOURCE, resource)
            .addArgument(I18n.OPTIGUI_RP_LOADER_WARN_UNSUPPORTED_JSON_FORMAT.supplyTranslation())
            .addArgument(resource.resource.format)
            .addArgument(I18n.OPTIGUI_RP_LOADER_WARN_NEWEST_SUPPORTED_JSON_FORMAT.supplyTranslation())
            .addArgument(JsonFilterResource.NEWEST_FORMAT)
            .log("{}: {}; {}: {}")
    }

    override fun iterator(): Iterator<IdentifiableResource<JsonFilterResource.V2>> = resources.iterator()
}
