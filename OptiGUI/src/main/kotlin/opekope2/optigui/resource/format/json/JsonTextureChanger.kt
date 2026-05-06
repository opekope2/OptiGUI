package opekope2.optigui.resource.format.json

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import opekope2.optigui.filter.texture_changer.ITextureChanger
import opekope2.optigui.filter.texture_changer.RandomizedTextureChanger
import opekope2.optigui.filter.texture_changer.SimpleTextureChanger
import opekope2.optigui.util.RelativeIdentifier

/**
 * The JSON-based representation of [ITextureChanger].
 *
 * @param newTextureChances A map containing the changed textures and a positive weight for the randomizer
 */
data class JsonTextureChanger(val newTextureChances: Map<Either<RelativeIdentifier, ResourceLocation>, Int>) {
    constructor(newTexture: Either<RelativeIdentifier, ResourceLocation>) : this(mapOf(newTexture to 1))
    constructor(newTexture: ResourceLocation) : this(Either.right(newTexture))

    init {
        require(newTextureChances.isNotEmpty()) { "New texture chances cannot be empty" }
        for ((_, value) in newTextureChances) require(value > 0) { "Weight must be positive: $value" }
    }

    /**
     * Creates an [ITextureChanger] from its JSON representation.
     *
     * If all textures are present, the result is [DataResult.Success].
     * If no textures are present, the result is [DataResult.Error].
     * Otherwise, the result is [DataResult.Error] with a partial result containing the present textures.
     *
     * @param textureResolver The predicate that checks if a texture is present in-game
     */
    inline fun createTextureChanger(textureResolver: (Either<RelativeIdentifier, ResourceLocation>) -> ResourceLocation?): DataResult<ITextureChanger> {
        val found = mutableMapOf<ResourceLocation, Int>()
        val notFound = mutableSetOf<String>()

        for ((texture, weight) in newTextureChances) {
            val resolved = textureResolver(texture)
            if (resolved != null) found[resolved] = weight
            else notFound += RelativeIdentifier.toString(texture)
        }

        return when (found.size) {
            newTextureChances.size ->
                if (newTextureChances.size == 1) DataResult.success(SimpleTextureChanger(found.keys.single()))
                else DataResult.success(RandomizedTextureChanger(found))

            0 -> DataResult.error { notFound.joinToString(prefix = "Missing textures: ") }

            1 -> DataResult.error(
                { notFound.joinToString(prefix = "Missing textures: ") },
                SimpleTextureChanger(found.keys.single())
            )

            else -> DataResult.error(
                { notFound.joinToString(prefix = "Missing textures: ") },
                RandomizedTextureChanger(found)
            )
        }
    }

    companion object {
        /**
         * A codec for [JsonTextureChanger].
         */
        @JvmField
        val CODEC: Codec<JsonTextureChanger> = Codec.either(
            RelativeIdentifier.RELATIVE_OR_ABSOLUTE_CODEC,
            Codec.unboundedMap(RelativeIdentifier.RELATIVE_OR_ABSOLUTE_CODEC, ExtraCodecs.POSITIVE_INT).validate {
                if (it.isNotEmpty()) DataResult.success(it)
                else DataResult.error { "No changed textures were specified" }
            }
        ).xmap(
            { either -> Either.unwrap(either.mapLeft { mapOf(it to 1) }) },
            { map ->
                if (map.size == 1) Either.left(map.keys.single())
                else Either.right(map)
            }
        ).xmap(::JsonTextureChanger, JsonTextureChanger::newTextureChances)
    }
}
