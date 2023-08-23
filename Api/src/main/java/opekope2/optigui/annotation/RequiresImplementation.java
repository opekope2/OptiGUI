package opekope2.optigui.annotation;

import opekope2.optigui.api.IOptiGuiApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that before using this function, the presence of the OptiGUI API should be checked using {@link IOptiGuiApi#isAvailable()}.
 *
 * @see IOptiGuiApi#getImplementation()
 * @see IOptiGuiApi#isAvailable()
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface RequiresImplementation {
}
