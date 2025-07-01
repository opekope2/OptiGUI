package opekope2.optigui.internal.fabric

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.VersionParsingException
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.internal.I18n
import com.mojang.serialization.Decoder as DfuDecoder

internal class NbtVersionFilter(
    private val version: Version,
    private val operator: VersionComparisonOperator,
    private val invert: Boolean
) : INbtFilter {
    override fun test(nbt: NbtElement): Boolean {
        if (nbt !is NbtString) return false
        val nbtVersion = parseVersion(nbt.asString()) ?: return false
        return operator.test(nbtVersion, version) xor invert
    }

    class Decoder(private val operator: VersionComparisonOperator, private val invert: Boolean = false) :
        DfuDecoder<NbtVersionFilter> {
        override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<NbtVersionFilter, T>> =
            ops.getStringValue(input).flatMap {
                parseVersion(it)?.let(DataResult<Version>::success)
                    ?: DataResult.error { I18n.OPTIGUI_RP_LOADER_ERROR_INVALID_VERSION.getTranslation(it) }
            }.map { NbtVersionFilter(it, operator, invert) }.map { Pair.of(it, ops.empty()) }
    }

    private companion object {
        private fun parseVersion(version: String) = try {
            Version.parse(version)
        } catch (_: VersionParsingException) {
            null
        }
    }
}
