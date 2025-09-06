package opekope2.optigui.internal.fabric

import com.mojang.serialization.Codec
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.VersionParsingException
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.filter.INbtFilter

internal class NbtVersionFilter(
    private val version: Version,
    private val operator: VersionComparisonOperator,
    private val invert: Boolean
) : INbtFilter {
    override fun test(nbt: NbtElement): Boolean {
        if (nbt !is NbtString) return false
        val nbtVersion = try {
            Version.parse(nbt.asString())
        } catch (_: VersionParsingException) {
            return false
        }
        return operator.test(nbtVersion, version) xor invert
    }

    companion object {
        fun codec(operator: VersionComparisonOperator, invert: Boolean = false): Codec<NbtVersionFilter> =
            Codecs.exceptionCatching(
                Codec.STRING.xmap(
                    { NbtVersionFilter(Version.parse(it), operator, invert) },
                    { it.version.friendlyString }
                )
            )
    }
}
