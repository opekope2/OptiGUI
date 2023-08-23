package opekope2.optigui.annotation;

import net.minecraft.entity.Entity;

import java.lang.annotation.*;

/**
 * Registers the annotated class as a processor for {@link #value()}.
 * The annotated class must implement {@link opekope2.optigui.api.interaction.IEntityProcessor},
 * and must be a registered entrypoint in {@code fabric.mod.json} in order to be recognized.
 * The name of the entrypoint is {@code optigui-entityprocessor}.
 *
 * @apiNote Can be used multiple times.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EntityProcessors.class)
public @interface EntityProcessor {
    /**
     * The class to register the processor for.
     */
    Class<? extends Entity> value();
}
