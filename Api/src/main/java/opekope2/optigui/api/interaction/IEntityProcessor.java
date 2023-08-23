package opekope2.optigui.api.interaction;

import net.minecraft.entity.Entity;
import opekope2.lilac.annotation.EntrypointName;
import opekope2.optigui.annotation.RequiresImplementation;
import opekope2.optigui.api.IOptiGuiApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Interface defining an entity processor.
 * <br>
 * To register an entity processor, see {@link opekope2.optigui.annotation.EntityProcessor} documentation.
 * The name of the entrypoint is {@code optigui-entityprocessor}.
 *
 * @param <T> The entity type the processor accepts
 * @see opekope2.optigui.annotation.EntityProcessor
 */
@EntrypointName("optigui-entityprocessor")
public interface IEntityProcessor<T extends Entity> extends Function<T, Object> {
    /**
     * Finds the registered entity processor for the given entity class.
     *
     * @param <T>  The type of the entity
     * @param type The class of the entity
     */
    @Nullable
    @RequiresImplementation
    static <T extends Entity> IEntityProcessor<T> ofClass(@NotNull Class<@NotNull T> type) {
        return IOptiGuiApi.getImplementation().getEntityProcessor(type);
    }
}
