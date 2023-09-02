@file: JvmName("Selectors")

package opekope2.optigui.internal.selector

import opekope2.lilac.exception.EntrypointException
import opekope2.lilac.util.Util
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.IFilter
import opekope2.util.ConflictHandlingMap
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger("OptiGUI/SelectorLoader")

internal lateinit var selectors: Map<String, ISelector>
    private set

@Suppress("unused")
fun loadSelectors() {
    if (::selectors.isInitialized) {
        logger.info("Not initializing selectors, because they are already initialized")
        return
    }

    val loadingSelectors = ConflictHandlingMap<String, ISelector>()

    Util.getEntrypointContainers(ISelector::class.java).forEach { selector ->
        val annotation = selector.entrypoint.javaClass.getAnnotation(Selector::class.java) ?: return@forEach
        loadingSelectors.reserveIfNeeded(annotation.value)
        loadingSelectors.put(annotation.value, selector.provider.metadata.id, selector.entrypoint)
    }

    if (loadingSelectors.conflicts) {
        val conflictTree = loadingSelectors.createConflictTree("Selector conflicts")

        logger.error("Multiple selectors were found for one or more key:\n$conflictTree")
        throw EntrypointException(
            "Multiple selectors were found for one or more key. This is not an OptiGUI bug. Some of the mods are incompatible with each other. See log for details"
        )
    }

    selectors = loadingSelectors.toMap()
}

private fun ConflictHandlingMap<String, ISelector>.reserveIfNeeded(selector: String) {
    if (selector in this) return

    if (selector == "replacement" ||
        selector == "load.priority" ||
        selector.startsWith("if.config.")
    ) {
        put(selector, "optigui", Reserved)
    }
}

private object Reserved : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? = null

    override fun toString(): String = "Reserved"
}
