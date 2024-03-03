package opekope2.optigui.internal.selector

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.fabricmc.loader.api.metadata.version.VersionPredicate
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.IFilter.Result.Companion.mismatch
import opekope2.optigui.filter.IFilter.Result.Companion.skip
import opekope2.optigui.filter.IFilter.Result.Match
import opekope2.optigui.internal.util.*
import opekope2.optigui.selector.ILoadTimeSelector
import kotlin.jvm.optionals.getOrNull

internal class ConditionalLoadTimeSelector : ILoadTimeSelector {
    override fun evaluate(value: String) = when (value.toBooleanStrictOrNull()) {
        true -> Match(null)
        false -> mismatch()
        null -> skip()
    }
}

internal class ModsLoadTimeSelector : ILoadTimeSelector {
    override fun evaluate(value: String): IFilter.Result<out Nothing?> {
        val modCheckResults = value.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.mapNotNull(::parseVersion) {
                throw RuntimeException("Invalid mod predicates: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.map { (modId, versionPredicate) ->
                FabricLoader.getInstance().getModContainer(modId).getOrNull()?.metadata?.version?.let(versionPredicate)
                    ?: false
            }
            ?: return skip()

        return if (modCheckResults.all { it }) Match(null)
        else mismatch()
    }

    private fun parseVersion(modWithVersion: String): Pair<String, (Version) -> Boolean>? {
        return try {
            val versionIndex = modWithVersion.indexOfAny(VersionComparisonOperator.entries.map { it.serialized })
            if (versionIndex < 0) {
                return modWithVersion to { true }
            }

            val modId = modWithVersion.substring(0 until versionIndex)
            val modVersion = modWithVersion.substring(versionIndex).ifEmpty { return modId to { true } }
            val versionPredicate = VersionPredicate.parse(modVersion)

            modId to versionPredicate::test
        } catch (e: Exception) {
            null
        }
    }
}
