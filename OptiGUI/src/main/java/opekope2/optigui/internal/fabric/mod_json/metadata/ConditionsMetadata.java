package opekope2.optigui.internal.fabric.mod_json.metadata;

import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record ConditionsMetadata(@NotNull Map<String, VersionPredicate> mods) {
    public ConditionsMetadata() {
        this(Map.of());
    }
}
