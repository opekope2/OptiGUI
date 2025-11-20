package opekope2.optigui.internal.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import opekope2.optigui.config.IConfig

internal class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory(IConfig::createConfigScreen)
}
