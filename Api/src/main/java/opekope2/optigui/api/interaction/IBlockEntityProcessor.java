package opekope2.optigui.api.interaction;

import net.minecraft.block.entity.BlockEntity;
import opekope2.lilac.annotation.EntrypointName;
import opekope2.optigui.annotation.RequiresImplementation;
import opekope2.optigui.api.IOptiGuiApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Interface defining an entity processor.
 * <br>
 * To register a block entity processor, see {@link opekope2.optigui.annotation.BlockEntityProcessor} documentation.
 * The name of the entrypoint is {@code optigui-blockentityprocessor}.
 *
 * @param <T> The entity type the processor accepts
 * @see opekope2.optigui.annotation.BlockEntityProcessor
 */
@EntrypointName("optigui-blockentityprocessor")
public interface IBlockEntityProcessor<T extends BlockEntity> extends Function<T, Object> {
    /**
     * Finds the registered block entity processor for the given block entity class.
     *
     * @param <T>  The type of the block entity
     * @param type The class of the block entity
     */
    @Nullable
    @RequiresImplementation
    static <T extends BlockEntity> IBlockEntityProcessor<T> ofClass(@NotNull Class<@NotNull T> type) {
        return IOptiGuiApi.getImplementation().getBlockEntityProcessor(type);
    }
}
