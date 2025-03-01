package opekope2.optigui.internal.fabric

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.VersionParsingException
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.minecraft.nbt.NbtString
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.matchNot
import opekope2.optigui.operator.INbtOperator
import opekope2.optigui.util.mapMessage
import opekope2.optigui.util.unwrap

internal open class NbtVersionOperator(private val operator: VersionComparisonOperator) : INbtOperator {
    override fun <T> createFilter(ops: DynamicOps<T>, input: T): DataResult<INbtFilter> {
        val string = ops.getStringValue(input).unwrap { return it.mapMessage() }
        val filterVersion = parseVersion(string) ?: return DataResult.error { "Invalid version: $string" }
        val filter = INbtFilter {
            if (it !is NbtString) return@INbtFilter false
            val version = parseVersion(it.asString()) ?: return@INbtFilter false
            operator.test(version, filterVersion)
        }
        return DataResult.success(filter)
    }

    private fun parseVersion(version: String) = try {
        Version.parse(version)
    } catch (_: VersionParsingException) {
        null
    }

    private class Inverted(operator: VersionComparisonOperator) : NbtVersionOperator(operator) {
        override fun <T> createFilter(ops: DynamicOps<T>, input: T): DataResult<INbtFilter> {
            return super.createFilter(ops, input).map(::matchNot)
        }
    }

    companion object {
        @JvmField
        val MORE_THAN = NbtVersionOperator(VersionComparisonOperator.GREATER)

        @JvmField
        val AT_LEAST = NbtVersionOperator(VersionComparisonOperator.GREATER_EQUAL)

        @JvmField
        val EQUAL_TO = NbtVersionOperator(VersionComparisonOperator.EQUAL)

        @JvmField
        val NOT_EQUAL_TO: NbtVersionOperator = Inverted(VersionComparisonOperator.EQUAL)

        @JvmField
        val AT_MOST = NbtVersionOperator(VersionComparisonOperator.LESS_EQUAL)

        @JvmField
        val LESS_THAN = NbtVersionOperator(VersionComparisonOperator.LESS)

        @JvmField
        val AT_LEAST_SAME_MINOR = NbtVersionOperator(VersionComparisonOperator.SAME_TO_NEXT_MINOR)

        @JvmField
        val AT_LEAST_SAME_MAJOR = NbtVersionOperator(VersionComparisonOperator.SAME_TO_NEXT_MAJOR)
    }
}
