package opekope2.optigui.internal.selector

import net.fabricmc.loader.api.SemanticVersion
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.fabricmc.loader.api.metadata.version.VersionPredicate
import opekope2.lilac.util.Util
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.selector.ILoadTimeSelector
import opekope2.optigui.filter.IFilter
import opekope2.util.*


@Selector("if")
class ConditionalLoadSelector : ILoadTimeSelector {
    override fun evaluate(value: String): IFilter.Result<out Any> =
        when (value.toBooleanStrictOrNull()) {
            true -> IFilter.Result.match(Unit)
            false -> IFilter.Result.mismatch()
            null -> IFilter.Result.skip()
        }
}

@Selector("if.mods")
class ModLoadSelector : ILoadTimeSelector {
    override fun evaluate(value: String): IFilter.Result<out Any> {
        val modCheckResults = value.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(::parseVersion) {
                throw RuntimeException("Invalid mod predicates: ${joinNotFound(it)}")
            }
            ?.map { (modId, versionPredicate) -> Util.checkModVersion(modId, versionPredicate) }
            ?: return IFilter.Result.skip<Unit>()

        return if (modCheckResults.all { it }) IFilter.Result.match(Unit)
        else IFilter.Result.mismatch()
    }

    private fun parseVersion(modWithVersion: String): Pair<String, (Version) -> Boolean>? {
        return try {
            val versionIndex = modWithVersion.indexOfAny(VersionComparisonOperator.values().map { it.serialized })
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

@Selector("if.mod.optigui.version.at_least")
@Deprecated("For backwards compatibility only")
class LegacyOptiGuiVersionLoadSelector : ILoadTimeSelector {
    override fun evaluate(value: String): IFilter.Result<out Any> {
        val requiredVersion = SemanticVersion.parse(value)
        return if (Util.checkModVersion("optigui") { v -> v >= requiredVersion }) IFilter.Result.match(Unit)
        else IFilter.Result.mismatch()
    }
}
