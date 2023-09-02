package opekope2.optigui.api.selector;

import opekope2.lilac.annotation.EntrypointName;
import opekope2.optigui.filter.IFilter;

/**
 * Interface defining a load time OptiGUI INI selector.
 * A load time selector runs when loading the resource, in contrast to when replacing a texture.
 * It is a {@link IFilter filter}, which decides whether the filter should be loaded or not.
 * <br>
 * A load time selector accepts the selector value in {@link ILoadTimeSelector#evaluate(Object)},
 * and returns a result indicating whether the resource should be loaded.
 * The result passed to {@link IFilter.Result#match(Object)} is ignored, it may be anything (Java doesn't have a unit type).
 * <br>
 * To register a load time selector, see {@link opekope2.optigui.annotation.Selector} documentation.
 * The name of the entrypoint is {@code optigui-load-selector}.
 *
 * @see opekope2.optigui.annotation.Selector
 */
@EntrypointName("optigui-load-selector")
public interface ILoadTimeSelector extends IFilter<String, Object> {
}
