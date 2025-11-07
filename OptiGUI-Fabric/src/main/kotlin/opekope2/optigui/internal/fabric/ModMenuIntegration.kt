package opekope2.optigui.internal.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.autoconfig.AutoConfig
import opekope2.optigui.internal.IOptiGuiPlatform

internal class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory { parent ->
        AutoConfig.getConfigScreen(IOptiGuiPlatform.configClass, parent).get()
    }
}
