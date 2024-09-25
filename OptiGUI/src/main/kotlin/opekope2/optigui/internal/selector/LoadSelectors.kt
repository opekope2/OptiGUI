package opekope2.optigui.internal.selector

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.fabricmc.loader.api.metadata.version.VersionPredicate
import opekope2.optigui.internal.util.*
import kotlin.jvm.optionals.getOrNull

internal class ConditionalLoadSelector : (String) -> Boolean {
    override fun invoke(selectorValue: String) = selectorValue.toBooleanStrict()
}

internal class ModsLoadSelector : (String) -> Boolean {
    override fun invoke(selectorValue: String): Boolean {
        val modCheckResults = selectorValue.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.mapNotNull(::parseVersion) {
                throw RuntimeException("Invalid mod predicates: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.map { (modId, versionPredicate) ->
                FabricLoader.getInstance().getModContainer(modId).getOrNull()?.metadata?.version?.let(versionPredicate)
                    ?: false
            }
            ?: return false

        return modCheckResults.all { it }
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
