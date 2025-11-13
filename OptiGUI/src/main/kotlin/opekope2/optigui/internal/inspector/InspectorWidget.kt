package opekope2.optigui.internal.inspector

import com.google.gson.GsonBuilder
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.Util
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.CommonInputs
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.debugger.Debugger
import opekope2.optigui.util.DEBUGGER_URL
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.mc
import org.jetbrains.annotations.ApiStatus
import java.util.concurrent.CompletableFuture

@ApiStatus.Internal
abstract class InspectorWidget : AbstractWidget(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXT) {
    private var customTextures = false
    private var prevFilter = InteractionManager.textureChangerFilter
    private var prevAlt = false
    private var prevHovered = false
    private var prevFocused = false

    private val debugger = object {
        var processing = false
            private set

        fun openInBrowser() {
            if (processing) return
            processing = true

            updateTooltip(DescriptionState.PROCESSING)
            CompletableFuture.supplyAsync(::generateDebugData)
                .thenApplyAsync(::openInBrowser, mc)
        }

        // Run on thread pool, because encoding a lot of filters to JSON can take up to hundreds of milliseconds
        private fun generateDebugData(): String? {
            return Debugger.getEncodedDebugData(InteractionManager.interaction ?: return null)
        }

        // Run on main thread because we're interacting with the UI and the clipboard
        private fun openInBrowser(debugData: String?) {
            if (debugData != null) {
                mc.keyboardHandler.clipboard = debugData
                Util.getPlatform().openUri(DEBUGGER_URL)
            }

            if (Screen.hasAltDown()) updateTooltip(DescriptionState.ofResult(debugData != null))
            processing = false
        }
    }

    protected abstract val generatedBy: String

    private fun updateTooltip(descriptionState: DescriptionState) {
        tooltip = InspectorTooltipStyle.CURRENT.createTooltip(customTextures, descriptionState)
    }

    override fun renderWidget(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val updateTooltip = when {
            customTextures != InteractionManager.hasRenderedCustomTextures -> true
            prevFilter != InteractionManager.textureChangerFilter -> true
            prevAlt != Screen.hasAltDown() -> true
            prevHovered != isHovered -> true
            prevFocused && !isFocused -> true
            else -> tooltip == null
        }
        customTextures = InteractionManager.hasRenderedCustomTextures
        prevFilter = InteractionManager.textureChangerFilter
        prevAlt = Screen.hasAltDown()
        prevHovered = isHovered
        prevFocused = isFocused

        if (updateTooltip) updateTooltip(DescriptionState.of(prevAlt, debugger.processing))

        val x = x + width / 2 - TEXTURE_WIDTH / 2
        val y = y + height / 2 - TEXTURE_HEIGHT / 2

        context.setColor(1f, 1f, 1f, alpha)
        RenderSystem.enableBlend()
        RenderSystem.enableDepthTest()
        context.blitSprite(TEXTURES.get(active, isHoveredOrFocused), x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT)
        context.setColor(1f, 1f, 1f, 1f)
    }

    private fun inspectInteraction() {
        val interaction = InteractionManager.interaction ?: return
        val json = Inspector.generateJsonResource(interaction, generatedBy)

        mc.keyboardHandler.clipboard = GSON.toJson(json)
        updateTooltip(DescriptionState.CLICKED)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        if (Screen.hasAltDown()) debugger.openInBrowser()
        else inspectInteraction()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (!active || !visible) return false
        if (!CommonInputs.selected(keyCode)) return false

        playDownSound(mc.soundManager)
        inspectInteraction()
        return true
    }

    override fun updateWidgetNarration(builder: NarrationElementOutput) {
        defaultButtonNarrationText(builder)
    }

    private companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        private val TEXT = I18n.OPTIGUI_INSPECTOR.getText()
        private const val TEXTURE_WIDTH = 38
        private const val TEXTURE_HEIGHT = 10
        private val TEXTURES = WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/inspector"),
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/inspector"),
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/inspector_focused")
        )
    }
}
