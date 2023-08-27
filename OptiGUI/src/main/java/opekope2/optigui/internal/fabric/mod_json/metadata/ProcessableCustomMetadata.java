package opekope2.optigui.internal.fabric.mod_json.metadata;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import opekope2.lilac.api.Util;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record ProcessableCustomMetadata(
        @NotNull Optional<Map<Identifier, Either<Identifier, List<Either<Identifier, ProcessableTexturePathMetadata>>>>> containerTextures
) {
    public static final Codec<ProcessableCustomMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    Identifier.CODEC,
                    Codec.either(
                            Identifier.CODEC,
                            Codec.either(
                                    Identifier.CODEC,
                                    ProcessableTexturePathMetadata.CODEC
                            ).listOf()
                    )
            ).optionalFieldOf("containerTextures").forGetter(ProcessableCustomMetadata::containerTextures)
    ).apply(instance, ProcessableCustomMetadata::new));

    public CustomMetadata process() {
        return containerTextures.map(identifierEitherMap -> new CustomMetadata(
                identifierEitherMap.entrySet().stream().collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                value -> value.getValue().map(
                                        l -> l,
                                        r -> r.stream().map(it -> it.map(
                                                        l -> new TexturePathMetadata(new ConditionsMetadata(), l),
                                                        ProcessableTexturePathMetadata::process
                                                ))
                                                .filter(ProcessableCustomMetadata::evaluateTexturePathConditions)
                                                .findFirst()
                                                .map(TexturePathMetadata::texture)
                                                .orElse(null)
                                )
                        )
                )
        )).orElseGet(CustomMetadata::new);
    }

    private static boolean evaluateTexturePathConditions(TexturePathMetadata texturePathMetadata) {
        return texturePathMetadata.conditions().mods().entrySet().stream().allMatch(
                entry -> Util.checkModVersion(entry.getKey(), entry.getValue()::test)
        );
    }
}
