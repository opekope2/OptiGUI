package opekope2.optigui.toast

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.toast.Toast
import net.minecraft.client.toast.ToastManager
import net.minecraft.text.Text

/**
 * A toast displaying the inspector message.
 */
class InspectorToast : Toast {
    override fun draw(context: DrawContext, manager: ToastManager, startTime: Long): Toast.Visibility {
        val textRenderer = manager.client.textRenderer
        context.drawTexture(Toast.TEXTURE, 0, 0, 0, 0, width, height)

        context.drawText(textRenderer, TITLE, 7, 7, 0xFF00FFFF.toInt(), false)
        context.drawText(textRenderer, DESCRIPTION, 7, 18, 0xFFFFFFFF.toInt(), false)
        return if (startTime >= 4000 * manager.notificationDisplayTimeMultiplier) Toast.Visibility.HIDE
        else Toast.Visibility.SHOW
    }

    companion object {
        private val TITLE = Text.translatable("optigui.toast.inspector.title")
        private val DESCRIPTION = Text.translatable("optigui.toast.inspector.description")
    }
}
