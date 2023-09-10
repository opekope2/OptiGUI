package opekope2.optigui.internal.fabric.mod_json.metadata;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record CustomMetadata(@NotNull Map<Identifier, Identifier> containerTextures) {
    CustomMetadata() {
        this(Map.of());
    }
}
