package opekope2.optigui.internal.inspector

import com.google.gson.GsonBuilder
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ButtonTextures
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.input.KeyCodes
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.I18n
import opekope2.optigui.internal.debugger.Debugger
import opekope2.optigui.util.DEBUGGER_URL
import opekope2.optigui.util.MOD_ID
import java.util.concurrent.CompletableFuture

@Environment(EnvType.CLIENT)
internal abstract class InspectorWidget : ClickableWidget(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXT) {
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
                .thenApplyAsync(::openInBrowser, MinecraftClient.getInstance())
        }

        // Run on thread pool, because encoding a lot of filters to JSON can take up to hundreds of milliseconds
        private fun generateDebugData(): String? {
            return Debugger.getEncodedDebugData(InteractionManager.interaction ?: return null)
        }

        // Run on main thread because we're interacting with the UI and the clipboard
        private fun openInBrowser(debugData: String?) {
            if (debugData != null) {
                MinecraftClient.getInstance().keyboard.clipboard = debugData
                Util.getOperatingSystem().open(DEBUGGER_URL)
            }

            if (Screen.hasAltDown()) updateTooltip(DescriptionState.ofResult(debugData != null))
            processing = false
        }
    }

    protected abstract val generatedBy: String

    private fun updateTooltip(descriptionState: DescriptionState) {
        tooltip = InspectorTooltipStyle.CURRENT.createTooltip(customTextures, descriptionState)
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
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

        context.setShaderColor(1f, 1f, 1f, alpha)
        RenderSystem.enableBlend()
        RenderSystem.enableDepthTest()
        context.drawGuiTexture(TEXTURES.get(active, isSelected), x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT)
        context.setShaderColor(1f, 1f, 1f, 1f)
    }

    private fun inspectInteraction() {
        val interaction = InteractionManager.interaction ?: return
        val json = Inspector.generateJsonResource(interaction, generatedBy)

        MinecraftClient.getInstance().keyboard.clipboard = GSON.toJson(json)
        updateTooltip(DescriptionState.CLICKED)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        if (Screen.hasAltDown()) debugger.openInBrowser()
        else inspectInteraction()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (!active || !visible) return false
        if (!KeyCodes.isToggle(keyCode)) return false

        playDownSound(MinecraftClient.getInstance().soundManager)
        inspectInteraction()
        return true
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
        appendDefaultNarrations(builder)
    }

    private companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        private val TEXT = I18n.OPTIGUI_INSPECTOR.getText()
        private const val TEXTURE_WIDTH = 38
        private const val TEXTURE_HEIGHT = 10
        private val TEXTURES = ButtonTextures(
            Identifier.of(MOD_ID, "widget/inspector"),
            Identifier.of(MOD_ID, "widget/inspector"),
            Identifier.of(MOD_ID, "widget/inspector_focused")
        )
    }
}
