@file: JvmName("Processors")

package opekope2.optigui.internal.interaction

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import opekope2.lilac.exception.EntrypointException
import opekope2.lilac.util.Util
import opekope2.optigui.annotation.BlockEntityProcessor
import opekope2.optigui.annotation.EntityProcessor
import opekope2.optigui.api.interaction.IBlockEntityProcessor
import opekope2.optigui.api.interaction.IEntityProcessor
import opekope2.util.ConflictHandlingMap
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("OptiGUI/ProcessorLoader")

internal lateinit var entityProcessors: Map<Class<out Entity>, IdentifiableEntityProcessor<out Entity>>
    private set
internal lateinit var blockEntityProcessors: Map<Class<out BlockEntity>, IdentifiableBlockEntityProcessor<out BlockEntity>>
    private set

internal val Entity.hasProcessor
    get() = javaClass in entityProcessors

internal val BlockEntity.hasProcessor
    get() = javaClass in blockEntityProcessors

@Suppress("unused")
fun loadEntityProcessors() {
    if (::entityProcessors.isInitialized) {
        logger.info("Not initializing entity processors, because they are already initialized")
        return
    }

    entityProcessors =
        loadProcessors<IEntityProcessor<Entity>, EntityProcessor, Entity, IdentifiableEntityProcessor<*>>(
            { annotation -> annotation.value.java },
            ::IdentifiableEntityProcessor
        )
}


@Suppress("unused")
fun loadBlockEntityProcessors() {
    if (::blockEntityProcessors.isInitialized) {
        logger.info("Not initializing block entity processors, because they are already initialized")
        return
    }

    blockEntityProcessors =
        loadProcessors<IBlockEntityProcessor<BlockEntity>, BlockEntityProcessor, BlockEntity, IdentifiableBlockEntityProcessor<*>>(
            { annotation -> annotation.value.java },
            ::IdentifiableBlockEntityProcessor
        )
}

private inline fun <reified TEntrypoint : Any, reified TAnnotation : Annotation, reified TProcessorClass, TProcessor> loadProcessors(
    annotationTransformer: (TAnnotation) -> Class<out TProcessorClass>,
    processorFactory: (String, TEntrypoint) -> TProcessor
): Map<Class<out TProcessorClass>, TProcessor> {
    val processors = ConflictHandlingMap<Class<out TProcessorClass>, TProcessor>()

    Util.getEntrypointContainers(TEntrypoint::class.java).forEach { processor ->
        processor.entrypoint.javaClass.getAnnotationsByType(TAnnotation::class.java).forEach { annotation ->
            try {
                processors.put(
                    annotationTransformer(annotation),
                    processor.provider.metadata.id,
                    processorFactory(processor.provider.metadata.id, processor.entrypoint)
                )
            } catch (e: TypeNotPresentException) {
                logger.warn("Ignoring `$annotation`: $e", e)
            }
        }
    }

    if (processors.conflicts) {
        val mapper = FabricLoader.getInstance().mappingResolver
        val processorClassName = mapper.unmapClassName("named", TProcessorClass::class.java.name).split('.').last()

        val conflictTree = processors.createConflictTree("$processorClassName processor conflicts").toString()

        logger.error("Multiple processors were found for one or more $processorClassName:\n$conflictTree")
        throw EntrypointException(
            "Multiple processors were found for one or more $processorClassName. This is not an OptiGUI bug. Some of the mods are incompatible with each other. See log for details"
        )
    }

    return processors.toMap()
}
