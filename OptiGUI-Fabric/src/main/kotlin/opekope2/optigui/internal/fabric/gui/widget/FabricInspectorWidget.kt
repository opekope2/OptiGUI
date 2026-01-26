package opekope2.optigui.internal.fabric.gui.widget

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import opekope2.optigui.internal.inspector.InspectorWidget
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen

internal class FabricInspectorWidget : InspectorWidget(), ScreenEvents.BeforeRender {
    override fun beforeRender(screen: Screen?, drawContext: GuiGraphics?, mouseX: Int, mouseY: Int, tickDelta: Float) {
        if (screen is ITextureChangeableScreen) screen.optiGui_positionInspectorWidget(this)
    }
}
