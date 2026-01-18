package opekope2.optigui.internal.ui

import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.DropdownComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Component.FocusSource
import io.wispforest.owo.ui.core.Positioning
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.config.config
import opekope2.optigui.internal.I18n
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.expandTemplate
import opekope2.optigui.util.mc
import opekope2.optigui.util.requireChildById
import io.wispforest.owo.config.ui.ConfigScreen as OwoConfigScreen

internal class ConfigScreen(parent: Screen?) : OwoConfigScreen(MODEL_ID, config, parent) {
    private val rootComponent get() = uiAdapter.rootComponent
    private val toolsButton by requireChildById<ButtonComponent>(::rootComponent)
    private var contextMenu: DropdownComponent? = null

    override fun build(rootComponent: FlowLayout) {
        super.build(rootComponent)

        toolsButton.onPress(::showToolsDropdown)
    }

    private fun showToolsDropdown(button: ButtonComponent) {
        if (contextMenu == null) {
            val toolsDropdown by model.expandTemplate<DropdownComponent>(mapOf())
            toolsDropdown.button(I18n.TEXT_CONFIG_OPTIGUI_TOOLS_RESOURCE_LOADING_LOGS.getText()) {
                hideContextMenu(); mc.setScreen(ResourceLoadingLogScreen(this))
            }
            showContextMenu(toolsDropdown, button.x(), button.y())
        } else {
            hideContextMenu()
        }
    }

    private fun hideContextMenu() {
        contextMenu?.remove()
        contextMenu = null
    }

    private fun showContextMenu(menu: DropdownComponent, x: Int, y: Int) {
        hideContextMenu()

        contextMenu = menu
        rootComponent.child(menu)

        val overflow = x + menu.width() - width
        menu.positioning(Positioning.absolute(x - overflow.coerceAtLeast(0), y - menu.height()))
        rootComponent.focusHandler()?.focus(menu, FocusSource.MOUSE_CLICK)
    }

    private companion object {
        private val MODEL_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "config")
    }
}
