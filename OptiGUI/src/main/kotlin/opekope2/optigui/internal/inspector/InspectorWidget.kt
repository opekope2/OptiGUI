package opekope2.optigui.internal.inspector

import com.google.gson.GsonBuilder
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ButtonTextures
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.input.KeyCodes
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.util.MOD_ID

@Environment(EnvType.CLIENT)
internal abstract class InspectorWidget : ClickableWidget(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXT) {
    private var customTextures = false
    private var prevHovered = false
    private var prevFocused = false

    protected abstract val generatedBy: String

    init {
        updateTooltip(false)
    }

    private fun updateTooltip(clickedDescription: Boolean) {
        tooltip = InspectorTooltipFactory.CURRENT.createTooltip(customTextures, clickedDescription)
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (customTextures != InteractionManager.hasRenderedCustomTextures) {
            customTextures = InteractionManager.hasRenderedCustomTextures
            updateTooltip(false)
        } else if (prevHovered != hovered || prevFocused && !isFocused) {
            updateTooltip(false)
        }
        prevHovered = isHovered
        prevFocused = isFocused

        val x = x + width / 2 - TEXTURE_WIDTH / 2
        val y = y + height / 2 - TEXTURE_HEIGHT / 2

        context.setShaderColor(1f, 1f, 1f, alpha)
        RenderSystem.enableBlend()
        RenderSystem.enableDepthTest()
        context.drawGuiTexture(TEXTURES.get(active, isSelected), x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT)
        context.setShaderColor(1f, 1f, 1f, 1f)
    }

    private fun inspectInteraction() {
        val json = generateJsonResource(generatedBy) ?: return

        MinecraftClient.getInstance().keyboard.clipboard = GSON.toJson(json)
        updateTooltip(true)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        inspectInteraction()
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
        private val GSON = GsonBuilder().setPrettyPrinting().create()
        private val TEXT = Text.translatable("optigui.inspector")
        private const val TEXTURE_WIDTH = 38
        private const val TEXTURE_HEIGHT = 10
        private val TEXTURES = ButtonTextures(
            Identifier.of(MOD_ID, "widget/inspector"),
            Identifier.of(MOD_ID, "widget/inspector"),
            Identifier.of(MOD_ID, "widget/inspector_focused")
        )
    }
}
