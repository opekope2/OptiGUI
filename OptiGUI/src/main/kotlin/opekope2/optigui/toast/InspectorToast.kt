package opekope2.optigui.toast

import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

/**
 * A toast displaying the inspector message.
 */
class InspectorToast : Toast {
    private var visibility = Toast.Visibility.HIDE

    override fun getWantedVisibility() = visibility

    override fun update(manager: ToastManager, time: Long) {
        visibility = if (time >= 4000 * manager.notificationDisplayTimeMultiplier) Toast.Visibility.HIDE
        else Toast.Visibility.SHOW
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, textRenderer: Font, startTime: Long) {
        context.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, width(), height())
        context.text(textRenderer, TITLE, 7, 7, 0xFF00FFFF.toInt(), false)
        context.text(textRenderer, DESCRIPTION, 7, 18, 0xFFFFFFFF.toInt(), false)
    }

    companion object {
        private val TEXTURE = Identifier.withDefaultNamespace("toast/advancement")
        private val TITLE = Component.translatable("optigui.toast.inspector.title")
        private val DESCRIPTION = Component.translatable("optigui.toast.inspector.description")
    }
}
