package opekope2.optigui.toast

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.toast.Toast
import net.minecraft.client.toast.ToastManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * A toast displaying the inspector message.
 */
class InspectorToast : Toast {
    private var visibility = Toast.Visibility.HIDE

    override fun getVisibility() = visibility

    override fun update(manager: ToastManager, time: Long) {
        visibility = if (time >= 4000 * manager.notificationDisplayTimeMultiplier) Toast.Visibility.HIDE
        else Toast.Visibility.SHOW
    }

    override fun draw(context: DrawContext, textRenderer: TextRenderer, startTime: Long) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, width, height)
        context.drawText(textRenderer, TITLE, 7, 7, 0xFF00FFFF.toInt(), false)
        context.drawText(textRenderer, DESCRIPTION, 7, 18, 0xFFFFFFFF.toInt(), false)
    }

    companion object {
        private val TEXTURE = Identifier.ofVanilla("toast/advancement")
        private val TITLE = Text.translatable("optigui.toast.inspector.title")
        private val DESCRIPTION = Text.translatable("optigui.toast.inspector.description")
    }
}
