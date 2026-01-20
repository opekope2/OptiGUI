package opekope2.optigui.internal.neoforge.gui.widget

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ScreenEvent
import opekope2.optigui.internal.inspector.InspectorWidget
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

internal class NeoForgeInspectorWidget : InspectorWidget() {
    init {
        FORGE_BUS.register(this)
    }

    @SubscribeEvent
    fun beforeRender(event: ScreenEvent.Render.Pre) {
        val screen = event.screen
        if (screen is ITextureChangeableScreen) screen.optiGui_positionInspectorWidget(this)
    }

    @SubscribeEvent
    fun onScreenClosing(event: ScreenEvent.Closing) {
        FORGE_BUS.unregister(this)
    }
}
