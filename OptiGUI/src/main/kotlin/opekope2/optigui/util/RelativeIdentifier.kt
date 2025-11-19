package opekope2.optigui.util

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import opekope2.optigui.internal.I18n

/**
 * A relative version of [ResourceLocation].
 *
 * @param path The relative path of the identifier. Must start with `./` and must be
 *   [a valid path][ResourceLocation.isValidPath]
 */
data class RelativeIdentifier(val path: String) {
    init {
        require(ResourceLocation.isValidPath(path)) { "Invalid characters in path: $path" }
        require(path.startsWith("./")) { "Path must start with './': $path" }
    }

    /**
     * Resolves the identifier relative to an absolute [ResourceLocation].
     *
     * @param relativeTo The identifier to resolve this identifier relative from.
     *   If it is a file, this identifier will be resolved relative to its parent directory.
     *   If it is a directory, it must end with `/`, and this identifier will be resolved relative to the directory
     *   itself
     */
    fun toIdentifier(relativeTo: ResourceLocation): ResourceLocation {
        val pathSepIndex = relativeTo.path.lastIndexOf('/')
        return if (pathSepIndex < 0) relativeTo.withPath(path.substring(2))
        else relativeTo.withPath(relativeTo.path.substring(0, pathSepIndex + 1) + path.substring(2))
    }

    override fun toString() = path

    companion object {
        /**
         * A codec for [RelativeIdentifier].
         */
        @JvmField
        val CODEC: Codec<RelativeIdentifier> = ExtraCodecs.RESOURCE_PATH_CODEC.validate {
            if (it.startsWith("./")) DataResult.success(it)
            else DataResult.error(I18n.OPTIGUI_VALIDATION_ERROR_NOT_A_RELATIVE_IDENTIFIER.supplyTranslation(it))
        }.xmap(::RelativeIdentifier, RelativeIdentifier::path)

        /**
         * A codec for either a [relative][RelativeIdentifier] or [absolute][ResourceLocation] identifier.
         */
        @JvmField
        val RELATIVE_OR_ABSOLUTE_CODEC: Codec<Either<RelativeIdentifier, ResourceLocation>> =
            Codec.either(CODEC, ResourceLocation.CODEC)

        /**
         * Resolves either a relative or absolute identifier relative to an absolute identifier.
         *
         * @see [RelativeIdentifier.toIdentifier]
         */
        @JvmStatic
        fun toIdentifier(
            identifier: Either<RelativeIdentifier, ResourceLocation>,
            relativeTo: ResourceLocation
        ): ResourceLocation = Either.unwrap(identifier.mapLeft { it.toIdentifier(relativeTo) })

        /**
         * Converts either a relative or absolute identifier to a string.
         */
        @JvmStatic
        fun toString(identifier: Either<RelativeIdentifier, ResourceLocation>): String =
            Either.unwrap(identifier.mapBoth(RelativeIdentifier::toString, ResourceLocation::toString))
    }
}
