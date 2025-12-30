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
    Iterable<IdentifiableResource<JsonFilterResource>> {
    private val resources = mutableListOf<IdentifiableResource<JsonFilterResource>>()

    fun addResource(resource: DataResult<IdentifiableResource<JsonFilterResource>>) {
        resource.ifSuccess(::addResource).ifError {
            val resource = it.resultOrPartial().getOrNull()
            if (resource == null) {
                logger.atError().addArgument(it.messageSupplier).log("{}") // This will go to unknown resources
                return@ifError
            }

            val loadFilter = resource.resource.loadFilter
            if (loadFilter.test(loadTimeNbt, loadTimeNbt)) logger.atError()
                .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                .addKeyValue(LOG_KEY_RESOURCE, resource.id)
                .addArgument(it.messageSupplier)
                .log("{}")
        }
    }

    fun addResource(resource: IdentifiableResource<JsonFilterResource>) {
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

    override fun iterator(): Iterator<IdentifiableResource<JsonFilterResource>> = resources.iterator()
}
