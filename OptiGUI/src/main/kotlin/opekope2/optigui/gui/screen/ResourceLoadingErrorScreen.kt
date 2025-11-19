package opekope2.optigui.gui.screen

import com.google.gson.GsonBuilder
import com.mojang.serialization.Dynamic
import com.mojang.serialization.JavaOps
import com.mojang.serialization.JsonOps
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Checkbox
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import opekope2.optigui.config.IConfig
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.gui.widget.ErrorListWidget
import opekope2.optigui.internal.I18n
import opekope2.optigui.util.ResourceLoadingLoggingEvent
import opekope2.optigui.util.ResourcePackIconLoader
import opekope2.optigui.util.mc
import org.slf4j.event.Level

/**
 * A screen that shows a list of resource loading log entries.
 *
 * @param log The resource loading log
 * @param onClose The action that gets called when this screen is closed
 */
class ResourceLoadingErrorScreen(private val log: List<ResourceLoadingLoggingEvent>, private val onClose: Runnable) :
    Screen(I18n.OPTIGUI_GUI_RP_LOADER_LOAD_FAIL.getText()) {
    private val layout = HeaderAndFooterLayout(this, HeaderAndFooterLayout.DEFAULT_HEADER_AND_FOOTER_HEIGHT, 58)
    private lateinit var errorsWidget: ErrorListWidget
    private lateinit var doNotShowAgainCheckbox: Checkbox
    private lateinit var copyButton: Button
    private lateinit var doneButton: Button

    private lateinit var iconLoader: ResourcePackIconLoader

    private fun encodeErrors(errors: List<ResourceLoadingLoggingEvent>): Dynamic<*> {
        val result = buildMap {
            for (error in errors) {
                getOrPut(
                    error.packName ?: I18n.OPTIGUI_GUI_RP_LOADER_UNKNOWN_PACK.getTranslation(),
                    ::mutableMapOf
                ).getOrPut(
                    error.resourceId?.toString() ?: I18n.OPTIGUI_GUI_RP_LOADER_UNKNOWN_RESOURCE.getTranslation(),
                    ::mutableMapOf
                ).getOrPut(
                    error.level.toString(),
                    ::mutableListOf
                ) += error.message
            }
        }
        return Dynamic(JavaOps.INSTANCE, result)
    }

    private fun copyErrors() {
        minecraft!!.keyboardHandler.clipboard =
            GSON.toJson(encodeErrors(errorsWidget.errors).convert(JsonOps.INSTANCE).value)
    }

    override fun init() {
        if (::iconLoader.isInitialized) iconLoader.close()
        iconLoader = ResourcePackIconLoader("resource_loading_error_screen", minecraft!!.textureManager)

        layout.addTitleHeader(title, font)

        errorsWidget = layout.addToContents(
            ErrorListWidget(
                font,
                minecraft!!.resourcePackRepository,
                iconLoader,
                log,
                0,
                layout.headerHeight,
                width - 40,
                layout.contentHeight
            )
        )

        val footer = layout.addToFooter(LinearLayout.vertical().spacing(5))
        footer.defaultCellSetting().alignHorizontallyCenter()
        doNotShowAgainCheckbox = footer.addChild(
            Checkbox.builder(I18n.OPTIGUI_GUI_RP_LOADER_DO_NOT_SHOW_AGAIN.getText(), font)
                .tooltip(Tooltip.create(I18n.OPTIGUI_GUI_RP_LOADER_DO_NOT_SHOW_AGAIN_TOOLTIP.getText()))
                .build()
        )

        val buttonBar = footer.addChild(LinearLayout.horizontal().spacing(5))
        copyButton = buttonBar.addChild(
            Button.builder(I18n.OPTIGUI_GUI_RP_LOADER_COPY_TO_CLIPBOARD.getText()) { copyErrors() }
                .build()
        )
        doneButton = buttonBar.addChild(
            Button.builder(CommonComponents.GUI_DONE) { onClose() }
                .build()
        )

        layout.visitWidgets(::addRenderableWidget)
        repositionElements()
    }

    override fun removed() {
        if (::iconLoader.isInitialized) iconLoader.close()
        if (doNotShowAgainCheckbox.selected()) {
            IConfig.get().showResourceLoadingErrors = IConfig.ResourceLoadingErrorFilter.NOTHING
            IConfig.get().save()
        }
    }

    override fun repositionElements() {
        layout.arrangeElements()
    }

    override fun onClose() {
        onClose.run()
    }

    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

        /**
         * Creates a runnable that changes the current screen when called.
         *
         * @param screen The screen to change to
         */
        @JvmStatic
        fun setScreen(screen: Screen?) = Runnable { mc.setScreen(screen) }

        /**
         * Creates a [ResourceLoadingErrorScreen] from the errors of the filter loaders registered in
         * [IFilterLoader.Registry].
         *
         * @param onClose The action that gets called when the returned screen is closed
         */
        @JvmStatic
        fun create(onClose: Runnable) = ResourceLoadingErrorScreen(IFilterLoader.flatMap { it.value.errors }, onClose)

        /**
         * Returns if the filter loaders registered in [IFilterLoader.Registry] have errors matching
         * [IConfig.showResourceLoadingErrors].
         */
        @JvmStatic
        fun shouldShow() = when (IConfig.get().showResourceLoadingErrors) {
            IConfig.ResourceLoadingErrorFilter.NOTHING -> false
            IConfig.ResourceLoadingErrorFilter.ERRORS_ONLY -> IFilterLoader.any { (_, loader) -> loader.errors.any { it.level == Level.ERROR } }
            IConfig.ResourceLoadingErrorFilter.ERRORS_AND_WARNINGS -> IFilterLoader.any { (_, loader) -> loader.errors.any { it.level == Level.ERROR || it.level == Level.WARN } }
        }

        /**
         * Shows a new [ResourceLoadingErrorScreen] If errors occurred while reloading resources.
         */
        @JvmStatic
        fun showIfErrorsOccurred() {
            if (!shouldShow()) return
            mc.setScreen(create(setScreen(mc.screen)))
        }
    }
}

