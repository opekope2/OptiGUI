package opekope2.optigui.internal.fabric.mod_json.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import opekope2.lilac.api.fabric.FabricCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public record ProcessableConditionsMetadata(@NotNull Optional<Map<String, VersionPredicate>> mods) {
    public static final Codec<ProcessableConditionsMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, FabricCodecs.VERSION_PREDICATE).optionalFieldOf("mods").forGetter(ProcessableConditionsMetadata::mods)
    ).apply(instance, ProcessableConditionsMetadata::new));

    public ConditionsMetadata process() {
        return mods.map(ConditionsMetadata::new).orElseGet(ConditionsMetadata::new);
    }
}
