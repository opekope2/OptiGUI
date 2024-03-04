package opekope2.optigui.selector

import opekope2.optigui.filter.IFilter
import opekope2.optigui.registry.LoadTimeSelectorRegistry

/**
 * Load-time selectors decide which resources should be loaded as filters, when the resources are (re)loaded
 * (when the game starts, the user changes resource packs, or presses F3+T).
 *
 * Each load-time selector accepts the string representation of a selector, parses and evaluates it, and returns a
 * result whether the resource should be loaded.
 *
 * @see LoadTimeSelectorRegistry
 */
interface ILoadTimeSelector : IFilter<String, Any?>
