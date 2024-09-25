package opekope2.optigui.toast

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.toast.Toast
import net.minecraft.client.toast.ToastManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

/**
 * A toast displaying the inspector message.
 */
class InspectorToast : Toast {
    override fun draw(matrices: MatrixStack?, manager: ToastManager, startTime: Long): Toast.Visibility {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderTexture(0, Toast.TEXTURE)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

        manager.drawTexture(matrices, 0, 0, 0, 0, width, height)
        manager.client.textRenderer.draw(matrices, TITLE, 7f, 7f, 0xFF00FFFF.toInt())
        manager.client.textRenderer.draw(matrices, DESCRIPTION, 7f, 18f, 0xFFFFFFFF.toInt())

        return if (startTime >= 4000) Toast.Visibility.HIDE
        else Toast.Visibility.SHOW
    }

    companion object {
        private val TITLE = Text.translatable("optigui.toast.inspector.title")
        private val DESCRIPTION = Text.translatable("optigui.toast.inspector.description")
    }
}
