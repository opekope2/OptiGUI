package opekope2.optigui.internal.fabric.mod_json.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ProcessableTexturePathMetadata(
        @NotNull Optional<ProcessableConditionsMetadata> conditions,
        @NotNull Identifier texture
) {
    public static final Codec<ProcessableTexturePathMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ProcessableConditionsMetadata.CODEC.optionalFieldOf("conditions").forGetter(ProcessableTexturePathMetadata::conditions),
            Identifier.CODEC.fieldOf("texture").forGetter(ProcessableTexturePathMetadata::texture)
    ).apply(instance, ProcessableTexturePathMetadata::new));

    public TexturePathMetadata process() {
        return conditions
                .map(metadata -> new TexturePathMetadata(metadata.process(), texture))
                .orElseGet(() -> new TexturePathMetadata(new ConditionsMetadata(), texture));
    }
}
