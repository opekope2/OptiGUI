package opekope2.optigui.internal.config.gui

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.Tooltip
import me.shedaniel.math.Point
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
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
    fieldName: Component,
    buttonText: Component,
    private val tooltipText: Component?,
    private val action: IAction
) : AbstractConfigListEntry<ButtonListEntry.IAction>(fieldName, false) {
    private val buttonWidget = Button.builder(buttonText) { action.run() }.bounds(0, 0, 150, 20).build()

    override fun getDefaultValue() = Optional.empty<IAction>()

    override fun narratables() = listOf(buttonWidget)

    override fun children() = listOf(buttonWidget)

    override fun getValue() = action

    override fun render(
        graphics: GuiGraphics,
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

        val window = Minecraft.getInstance().window
        this.buttonWidget.y = y
        val displayedFieldName = this.displayedFieldName
        if (Minecraft.getInstance().font.isBidirectional) {
            graphics.drawString(
                Minecraft.getInstance().font,
                displayedFieldName.visualOrderText,
                window.guiScaledWidth - x - Minecraft.getInstance().font.width(displayedFieldName),
                y + 6,
                0xFFFFFF
            )
            this.buttonWidget.x = x + 2
        } else {
            graphics.drawString(
                Minecraft.getInstance().font,
                displayedFieldName.visualOrderText,
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
