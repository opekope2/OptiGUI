package opekope2.optigui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for {@link EntityProcessor}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityProcessors {
    /**
     * Gets all {@link EntityProcessor} annotations.
     */
    EntityProcessor[] value();
}
