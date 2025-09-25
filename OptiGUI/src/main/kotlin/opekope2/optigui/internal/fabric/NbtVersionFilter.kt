package opekope2.optigui.internal.fabric

import com.mojang.serialization.Codec
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.VersionParsingException
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.filter.INbtFilter

internal class NbtVersionFilter(private val version: Version, override val type: Type) : INbtFilter {
    override fun test(nbt: NbtElement, root: NbtElement): Boolean {
        if (nbt !is NbtString) return false
        val nbtVersion = try {
            Version.parse(nbt.asString())
        } catch (_: VersionParsingException) {
            return false
        }
        return type.operator.test(nbtVersion, version) xor type.invert
    }

    enum class Type(val operator: VersionComparisonOperator, val invert: Boolean = false) :
        INbtFilter.IType<NbtVersionFilter> {
        VERSION_GREATER_EQUAL(VersionComparisonOperator.GREATER_EQUAL),
        VERSION_LESS_EQUAL(VersionComparisonOperator.LESS_EQUAL),
        VERSION_GREATER(VersionComparisonOperator.GREATER),
        VERSION_LESS(VersionComparisonOperator.LESS),
        VERSION_EQUAL(VersionComparisonOperator.EQUAL),
        VERSION_SAME_TO_NEXT_MINOR(VersionComparisonOperator.SAME_TO_NEXT_MINOR),
        VERSION_SAME_TO_NEXT_MAJOR(VersionComparisonOperator.SAME_TO_NEXT_MAJOR),

        VERSION_NOT_EQUAL(VersionComparisonOperator.EQUAL, invert = true);

        override val codec: Codec<NbtVersionFilter> = Codecs.exceptionCatching(
            Codec.STRING.xmap(
                { NbtVersionFilter(Version.parse(it), this) },
                { it.version.friendlyString }
            )
        )
    }
}
