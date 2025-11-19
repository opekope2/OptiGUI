package opekope2.optigui.internal.neoforge.filter

import com.mojang.serialization.Codec
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.comparer.INbtComparer
import opekope2.optigui.filter.comparer.INbtComparer.ComparisonResult.*
import org.apache.maven.artifact.versioning.ArtifactVersion
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import thedarkcolour.kotlinforforge.neoforge.kotlin.enumSet
import thedarkcolour.kotlinforforge.neoforge.kotlin.enumSetOf
import java.util.*

internal class NbtVersionFilter(private val version: ArtifactVersion, override val type: Type) : INbtFilter {
    override fun test(nbt: Tag, root: Tag): Boolean {
        if (nbt !is StringTag) return false
        val nbtVersion = DefaultArtifactVersion(nbt.asString)
        return type.test(nbtVersion, version)
    }

    override fun asString() = super.asString() + " " + version.toString()

    enum class Type(
        val acceptedResults: EnumSet<INbtComparer.ComparisonResult>,
        val requireSameMajor: Boolean = false,
        val requireSameMinor: Boolean = false
    ) : INbtFilter.IType<NbtVersionFilter> {
        VERSION_GREATER_EQUAL(MORE, EQUAL),
        VERSION_LESS_EQUAL(LESS, EQUAL),
        VERSION_GREATER(MORE),
        VERSION_LESS(LESS),
        VERSION_EQUAL(EQUAL),
        VERSION_SAME_TO_NEXT_MINOR(enumSetOf(MORE, EQUAL), requireSameMajor = true),
        VERSION_SAME_TO_NEXT_MAJOR(enumSetOf(MORE, EQUAL), requireSameMajor = true, requireSameMinor = true),
        VERSION_NOT_EQUAL(MORE, LESS);

        constructor(vararg acceptedResults: INbtComparer.ComparisonResult) : this(acceptedResults.toCollection(enumSet()))

        override val codec: Codec<NbtVersionFilter> = Codec.STRING.xmap(
            { NbtVersionFilter(DefaultArtifactVersion(it), this) },
            { it.version.toString() }
        )

        fun test(version: ArtifactVersion, reference: ArtifactVersion) =
            INbtComparer.ComparisonResult.ofComparison(reference.compareTo(version)) in acceptedResults &&
                    (!requireSameMajor || version.majorVersion == reference.majorVersion) &&
                    (!requireSameMinor || version.minorVersion == reference.minorVersion)
    }
}
