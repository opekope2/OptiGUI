package opekope2.optigui.util

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.internal.I18n

/**
 * A relative version of [Identifier].
 *
 * @param path The relative path of the identifier. Must start with `./` and must be
 *   [a valid path][Identifier.isPathValid]
 */
data class RelativeIdentifier(val path: String) {
    init {
        require(Identifier.isPathValid(path)) { "Invalid characters in path: $path" }
        require(path.startsWith("./")) { "Path must start with './': $path" }
    }

    /**
     * Resolves the identifier relative to an absolute [Identifier].
     *
     * @param relativeTo The identifier to resolve this identifier relative from.
     *   If it is a file, this identifier will be resolved relative to its parent directory.
     *   If it is a directory, it must end with `/`, and this identifier will be resolved relative to the directory
     *   itself
     */
    fun toIdentifier(relativeTo: Identifier): Identifier {
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
        val CODEC: Codec<RelativeIdentifier> = Codecs.IDENTIFIER_PATH.validate {
            if (it.startsWith("./")) DataResult.success(it)
            else DataResult.error { I18n.OPTIGUI_VALIDATION_ERROR_NOT_A_RELATIVE_IDENTIFIER.getTranslation(it) }
        }.xmap(::RelativeIdentifier, RelativeIdentifier::path)

        /**
         * A codec for either a [relative][RelativeIdentifier] or [absolute][Identifier] identifier.
         */
        @JvmField
        val RELATIVE_OR_ABSOLUTE_CODEC: Codec<Either<RelativeIdentifier, Identifier>> =
            Codec.either(CODEC, Identifier.CODEC)

        /**
         * Resolves either a relative or absolute identifier relative to an absolute identifier.
         *
         * @see [RelativeIdentifier.toIdentifier]
         */
        @JvmStatic
        fun toIdentifier(identifier: Either<RelativeIdentifier, Identifier>, relativeTo: Identifier): Identifier =
            Either.unwrap(identifier.mapLeft { it.toIdentifier(relativeTo) })

        /**
         * Converts either a relative or absolute identifier to a string.
         */
        @JvmStatic
        fun toString(identifier: Either<RelativeIdentifier, Identifier>): String =
            Either.unwrap(identifier.mapBoth(RelativeIdentifier::toString, Identifier::toString))
    }
}
