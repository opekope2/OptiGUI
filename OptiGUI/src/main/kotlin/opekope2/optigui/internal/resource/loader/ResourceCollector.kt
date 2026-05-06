package opekope2.optigui.internal.resource.loader

import com.mojang.serialization.DataResult
import net.minecraft.nbt.CompoundTag
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.LOG_KEY_RESOURCE
import opekope2.optigui.util.LOG_KEY_RESOURCE_PACK
import org.slf4j.Logger
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.jvm.optionals.getOrNull

internal class ResourceCollector(private val logger: Logger, private val loadTimeNbt: CompoundTag) :
    Iterable<IdentifiableResource<JsonFilterResource>> {
    private val resources = ConcurrentLinkedDeque<IdentifiableResource<JsonFilterResource>>()

    fun addResource(resource: DataResult<IdentifiableResource<JsonFilterResource>>) {
        resource.ifSuccess(::addResource).ifError {
            val resource = it.resultOrPartial().getOrNull()
            if (resource == null) {
                logger.atError().addArgument(it.messageSupplier).log("{}") // This will go to unknown resources
            } else {
                val loadFilter = resource.resource.loadFilter
                if (loadFilter.test(loadTimeNbt, loadTimeNbt)) logger.atError()
                    .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                    .addKeyValue(LOG_KEY_RESOURCE, resource.id)
                    .addArgument(it.messageSupplier)
                    .log("{}")
            }
        }
    }

    fun addResource(resource: IdentifiableResource<JsonFilterResource>) {
        val json = resource.resource

        when {
            json.blocks.isEmpty() && json.entities.isEmpty() && json.items.isEmpty() && !json.inventory && !json.unknown -> logger.atWarn()
                .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                .addKeyValue(LOG_KEY_RESOURCE, resource.id)
                .log("No valid 'blocks', 'entities', 'items', 'inventory', or 'unknown' were specified")

            !json.loadFilter.test(loadTimeNbt, loadTimeNbt) -> logger.atInfo()
                .addKeyValue(LOG_KEY_RESOURCE_PACK, resource.packId)
                .addKeyValue(LOG_KEY_RESOURCE, resource.id)
                .log("Skipping resource because of load-time filter")

            else -> resources += resource
        }
    }

    override fun iterator(): Iterator<IdentifiableResource<JsonFilterResource>> = resources.iterator()
}
