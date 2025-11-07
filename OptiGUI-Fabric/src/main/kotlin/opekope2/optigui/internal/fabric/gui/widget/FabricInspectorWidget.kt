package opekope2.optigui.internal.fabric.gui.widget

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import opekope2.optigui.internal.inspector.InspectorWidget
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen
import opekope2.optigui.util.MOD_ID
import kotlin.jvm.optionals.getOrNull

internal class FabricInspectorWidget : InspectorWidget(), ScreenEvents.BeforeRender {
    override val generatedBy: String
        get() = GENERATED_BY

    override fun beforeRender(screen: Screen?, drawContext: DrawContext?, mouseX: Int, mouseY: Int, tickDelta: Float) {
        if (screen is ITextureChangeableScreen) screen.optiGui_positionInspectorWidget(this)
    }

    private companion object {
        private val GENERATED_BY = run {
            val modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).getOrNull() ?: return@run "OptiGUI"
            val modVersion = modContainer.metadata.version.toString()
            "OptiGUI $modVersion"
        }
    }
}
