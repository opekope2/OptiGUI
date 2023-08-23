package opekope2.optigui.api.interaction;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Represents the target of an interaction. See nested classes for available options.
 */
public sealed interface IInteractionTarget {
    /**
     * Calculates the interaction data by the given target entity.
     *
     * @see IEntityProcessor
     * @see IBlockEntityProcessor
     */
    @Nullable
    Object computeInteractionData();

    /**
     * Represents an interaction without a target.
     * <br>
     * The interaction data returned by {@link #computeInteractionData()} will always be {@code null}.
     */
    @NotNull
    IInteractionTarget NoneTarget = new ComputedTarget((Object) null);

    /**
     * Represents the target of an interaction as a block entity.
     *
     * @param blockEntity The target of the interaction
     * @apiNote A processor must be registered according to {@link opekope2.optigui.annotation.BlockEntityProcessor}.
     * Processors for vanilla block entities are added by OptiGUI automatically.
     */
    record BlockEntityTarget(@NotNull BlockEntity blockEntity) implements IInteractionTarget {
        public BlockEntityTarget {
            if (IBlockEntityProcessor.ofClass(blockEntity.getClass()) == null) {
                throw new IllegalArgumentException("No processor has been registered for %s.".formatted(blockEntity.getClass()));
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        @Nullable
        public Object computeInteractionData() {
            IBlockEntityProcessor<BlockEntity> processor = (IBlockEntityProcessor<BlockEntity>) IBlockEntityProcessor.ofClass(blockEntity.getClass());
            assert processor != null;
            return processor.apply(blockEntity);
        }
    }

    /**
     * Represents the target of an interaction as an entity.
     *
     * @param entity The target of the interaction
     * @apiNote A processor must be registered according to {@link opekope2.optigui.annotation.EntityProcessor}.
     * Processors for vanilla entities are added by OptiGUI automatically.
     */
    record EntityTarget(@NotNull Entity entity) implements IInteractionTarget {
        public EntityTarget {
            if (IEntityProcessor.ofClass(entity.getClass()) == null) {
                throw new IllegalArgumentException("No processor has been registered for %s.".formatted(entity.getClass()));
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        @Nullable
        public Object computeInteractionData() {
            IEntityProcessor<Entity> processor = (IEntityProcessor<Entity>) IEntityProcessor.ofClass(entity.getClass());
            assert processor != null;
            return processor.apply(entity);
        }
    }

    /**
     * An interaction target, which gets computed on each game tick,
     * so be sure to write optimized code in order to avoid performance issues.
     *
     * @param computeFunction The implementation of {@link #computeInteractionData()}
     */
    record ComputedTarget(@NotNull Supplier<Object> computeFunction) implements IInteractionTarget {
        /**
         * @param interactionData The interaction data to be returned by {@link #computeInteractionData()}
         */
        public ComputedTarget(@Nullable Object interactionData) {
            this(() -> interactionData);
        }

        @Override
        @Nullable
        public Object computeInteractionData() {
            return computeFunction.get();
        }
    }
}
