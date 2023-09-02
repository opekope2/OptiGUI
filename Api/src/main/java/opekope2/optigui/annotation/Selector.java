package opekope2.optigui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers the annotated class as an OptiGUI INI selector for the key specified in {@link #value()}.
 * The annotated class must implement {@link opekope2.optigui.api.selector.ISelector},
 * and must be a registered entrypoint in {@code fabric.mod.json} in order to be recognized.
 * The name of the entrypoint is {@code optigui-selector}.
 *
 * @apiNote Only Java/Kotlin classes and Kotlin (companion) objects are supported.
 * Function and field references will not be recognized.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Selector {
    /**
     * The name of the selector (the key to look for inside the INI file).
     *
     * @apiNote If the selector starts with {@code if.}, then it will be evaluated when loading the INI file,
     * and the current section of the INI file will only be loaded, if the filter returns a {@link opekope2.optigui.filter.FilterResult.Match}
     */
    String value();
}
