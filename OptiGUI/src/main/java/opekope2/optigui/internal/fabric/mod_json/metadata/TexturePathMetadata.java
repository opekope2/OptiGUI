package opekope2.optigui.internal.fabric.mod_json.metadata;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record TexturePathMetadata(@NotNull ConditionsMetadata conditions, @NotNull Identifier texture) {
}
