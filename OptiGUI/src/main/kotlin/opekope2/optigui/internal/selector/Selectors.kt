@file: JvmName("Selectors")

package opekope2.optigui.internal.selector

import opekope2.lilac.exception.EntrypointException
import opekope2.lilac.util.Util
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ILoadTimeSelector
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.IFilter
import opekope2.util.ConflictHandlingMap
import opekope2.util.IIdentifiable
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger("OptiGUI/SelectorLoader")

internal lateinit var selectors: Map<String, IdentifiableSelector>
    private set

internal lateinit var loadTimeSelectors: Map<String, IdentifiableLoadTimeSelector>
    private set

@Suppress("unused")
fun loadSelectors() {
    if (::selectors.isInitialized) {
        logger.info("Not initializing selectors, because they are already initialized")
        return
    }

    selectors = loadSelectorsOfType<ISelector, IdentifiableSelector>(::IdentifiableSelector) { selector ->
        if (selector in this) return@loadSelectorsOfType

        if (selector == "replacement" ||
            selector == "load.priority" ||
            selector == "if" ||
            selector.startsWith("if.")
        ) {
            put(selector, IdentifiableSelector("optigui", Reserved))
        }
    }
}

@Suppress("unused")
fun loadLoadTimeSelectors() {
    if (::loadTimeSelectors.isInitialized) {
        logger.info("Not initializing load time selectors, because they are already initialized")
        return
    }

    loadTimeSelectors =
        loadSelectorsOfType<ILoadTimeSelector, IdentifiableLoadTimeSelector>(::IdentifiableLoadTimeSelector) { selector ->
            if (selector in this) return@loadSelectorsOfType

            if (selector == "if") {
                // Allow load time selector `if`
            } else if (!selector.startsWith("if.") ||
                selector.startsWith("if.config.")
            ) {
                put(selector, IdentifiableLoadTimeSelector("optigui", Reserved))
            }
        }
}

private inline fun <reified TEntryPoint : Any, reified TSelector : IIdentifiable> loadSelectorsOfType(
    convertSelector: (String, TEntryPoint) -> TSelector,
    reserveIfNeeded: ConflictHandlingMap<String, TSelector>.(String) -> Unit
): Map<String, TSelector> {
    val selectors = ConflictHandlingMap<String, TSelector>()

    Util.getEntrypointContainers(TEntryPoint::class.java).forEach { selector ->
        val annotation = selector.entrypoint.javaClass.getAnnotation(Selector::class.java) ?: return@forEach
        selectors.reserveIfNeeded(annotation.value)
        selectors[annotation.value] = convertSelector(selector.provider.metadata.id, selector.entrypoint)
    }

    if (selectors.conflicts) {
        val selectorType = TSelector::class.java.name
        val conflictTree = selectors.createConflictTree("$selectorType conflicts")

        logger.error("Multiple ${selectorType}s were found for one or more key:\n$conflictTree")
        throw EntrypointException(
            "Multiple ${selectorType}s were found for one or more key. This is not an OptiGUI bug. Some of the mods are incompatible with each other. See log for details"
        )
    }

    return selectors.toMap()
}

internal class IdentifiableSelector(override val id: String, selector: ISelector) : ISelector by selector, IIdentifiable

internal class IdentifiableLoadTimeSelector(override val id: String, selector: ILoadTimeSelector) :
    ILoadTimeSelector by selector, IIdentifiable

private object Reserved : ISelector, ILoadTimeSelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? = null

    override fun evaluate(value: String?): IFilter.Result<out Any> = IFilter.Result.match(Unit)

    override fun toString(): String = "Reserved"
}
