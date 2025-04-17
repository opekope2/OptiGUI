package opekope2.optigui.config.gui

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.Tooltip
import me.shedaniel.math.Point
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import java.util.*

/**
 * A cloth config list entry with a button.
 *
 * @param fieldName The display text of the config field
 * @param buttonText The display text of the button
 * @param tooltipText The tooltip on the button or `null` to disable
 * @param action The action that gets called when the user clicks the button
 */
class ButtonListEntry(
    fieldName: Text,
    buttonText: Text,
    private val tooltipText: Text?,
    private val action: IAction
) : AbstractConfigListEntry<ButtonListEntry.IAction>(fieldName, false) {
    private val buttonWidget = ButtonWidget.builder(buttonText) { action.run() }.dimensions(0, 0, 150, 20).build()

    override fun getDefaultValue() = Optional.empty<IAction>()

    override fun narratables() = listOf(buttonWidget)

    override fun children() = listOf(buttonWidget)

    override fun getValue() = action

    override fun render(
        graphics: DrawContext,
        index: Int,
        y: Int,
        x: Int,
        entryWidth: Int,
        entryHeight: Int,
        mouseX: Int,
        mouseY: Int,
        isHovered: Boolean,
        delta: Float
    ) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta)

        val window = MinecraftClient.getInstance().window
        this.buttonWidget.y = y
        val displayedFieldName = this.displayedFieldName
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft) {
            graphics.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                displayedFieldName.asOrderedText(),
                window.scaledWidth - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName),
                y + 6,
                0xFFFFFF
            )
            this.buttonWidget.x = x + 2
        } else {
            graphics.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                displayedFieldName.asOrderedText(),
                x,
                y + 6,
                this.preferredTextColor
            )
            this.buttonWidget.x = x + entryWidth - 150
        }

        this.buttonWidget.render(graphics, mouseX, mouseY, delta)

        if (tooltipText != null && isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            addTooltip(Tooltip.of(Point(mouseX, mouseY), tooltipText))
        }
    }

    /**
     * A button's click handler
     */
    fun interface IAction : Runnable
}
