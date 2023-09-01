package opekope2.optigui.internal.lilac_resource_loader

import net.minecraft.util.Identifier
import opekope2.lilac.api.resource.IResourceAccess
import opekope2.lilac.api.resource.loading.IResourceLoader
import opekope2.lilac.api.resource.loading.IResourceLoadingSession
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.lilac_resource_loading.IOptiGuiExtension
import opekope2.optigui.filter.Filter
import opekope2.optigui.filter.FirstMatchFilter
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.internal.filter.IdentifiablePriorityFilter
import org.slf4j.LoggerFactory

@Suppress("unused")
class OptiGuiResourceLoadingSession private constructor(private val session: IResourceLoadingSession) {
    private val filters = mutableListOf<IdentifiablePriorityFilter>()
    private var closed = false

    @Suppress("NOTHING_TO_INLINE")
    private inline fun ensureNotClosed() {
        if (closed) {
            throw IllegalStateException("This object is already closed")
        }
    }

    private fun addFilter(
        modId: String,
        resource: Identifier,
        filter: Filter<Interaction, Identifier>,
        replaceableTextures: MutableSet<Identifier>,
        priority: Int
    ) {
        ensureNotClosed()
        filters += IdentifiablePriorityFilter(
            modId,
            resourceAccess.getResource(resource),
            filter,
            replaceableTextures,
            priority
        )
    }

    private fun warn(modId: String, resource: Identifier, message: String) {
        ensureNotClosed()
        logger.warn("`$modId` loading `$resource`: $message") // TODO add GUI
    }

    private fun applyFiltersAndClose() {
        ensureNotClosed()
        filters.sortByDescending { filter -> filter.priority }

        val filter = FirstMatchFilter(filters)
        TextureReplacer.filter = filter
        TextureReplacer.replaceableTextures = filters.flatMap { it.replaceableTextures }.toSet()

        logger.debug("Loaded filter chain:\n{}", filter.createTree())

        closed = true
    }

    companion object {
        private val logger = LoggerFactory.getLogger("OptiGUI/ResourceLoader")
        private val resourceAccess = IResourceAccess.getInstance()
        private val sessions = mutableMapOf<IResourceLoadingSession, OptiGuiResourceLoadingSession>()
    }

    private inner class Handle(private val modId: String) : IOptiGuiExtension {
        private var closed = false

        override fun addFilter(
            resource: Identifier,
            filter: Filter<Interaction, Identifier>,
            replaceableTextures: MutableSet<Identifier>,
            priority: Int
        ) = addFilter(modId, resource, filter, replaceableTextures, priority)

        override fun warn(resource: Identifier, message: String) = warn(modId, resource, message)

        override fun close() {
            if (closed) {
                throw IllegalStateException("This object is already closed")
            }

            closed = true
        }
    }

    class ExtensionFactory : IResourceLoadingSession.IExtensionFactory,
        IResourceLoadingSession.ILifecycleListener {
        override fun createSessionExtension(
            modId: String,
            factory: IResourceLoader.IFactory,
            session: IResourceLoadingSession
        ): Any {
            assert(IResourceLoadingSession.getProperties(session).stage == IResourceLoadingSession.Stage.INIT)

            return sessions[session]?.Handle(modId) ?: throw IllegalArgumentException("Invalid session")
        }

        override fun onCreated(session: IResourceLoadingSession) {
        }

        override fun onStageChanged(session: IResourceLoadingSession) {
            if (session !in sessions && IResourceLoadingSession.getProperties(session).stage == IResourceLoadingSession.Stage.INIT) {
                sessions[session] = OptiGuiResourceLoadingSession(session)
            }
        }

        override fun onClosed(session: IResourceLoadingSession) {
            if (session in sessions && IResourceLoadingSession.getProperties(session).stage == IResourceLoadingSession.Stage.INACTIVE) {
                sessions.remove(session)!!.applyFiltersAndClose()
            }
        }
    }
}
