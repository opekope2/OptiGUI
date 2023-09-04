package opekope2.optigui.internal.selector

import net.fabricmc.loader.api.SemanticVersion
import opekope2.lilac.util.Util
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.selector.ILoadTimeSelector
import opekope2.optigui.filter.IFilter


@Selector("if.mod.optigui.version.at_least")
@Deprecated("For backwards compatibility only")
class LegacyOptiGuiVersionLoadSelector : ILoadTimeSelector {
    override fun evaluate(value: String): IFilter.Result<out Any> {
        val requiredVersion = SemanticVersion.parse(value)
        return if (Util.checkModVersion("optigui") { v -> v >= requiredVersion }) IFilter.Result.match(Unit)
        else IFilter.Result.mismatch()
    }
}
