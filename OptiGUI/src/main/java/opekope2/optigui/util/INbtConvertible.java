package opekope2.optigui.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object, which can be written to an [NbtCompound].
 */
public interface INbtConvertible {
    /**
     * Writes the object's content to {@code compound}.
     *
     * @param compound The output {@link NbtCompound} to write contents to
     * @param lookup   The registry lookup used to encode NBT
     */
    void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup);
}
