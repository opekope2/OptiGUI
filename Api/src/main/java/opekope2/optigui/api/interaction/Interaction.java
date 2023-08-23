package opekope2.optigui.api.interaction;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interaction representation with a block entity or entity.
 *
 * @param texture        The texture to be replaced.
 * @param screenTitle    The active GUI screen's title
 * @param rawInteraction The raw interaction from Minecraft containing the details
 * @param data           The interaction data returned by the interaction data provider
 * @see IEntityProcessor
 * @see IBlockEntityProcessor
 * @see RawInteraction
 */
public record Interaction(
        @NotNull Identifier texture,
        @NotNull Text screenTitle,
        @Nullable RawInteraction rawInteraction,
        @Nullable Object data
) {
}
