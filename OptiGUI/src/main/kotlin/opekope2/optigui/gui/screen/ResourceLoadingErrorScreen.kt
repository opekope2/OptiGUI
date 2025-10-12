package opekope2.optigui.gui.screen

import com.google.gson.GsonBuilder
import com.mojang.serialization.Dynamic
import com.mojang.serialization.JavaOps
import com.mojang.serialization.JsonOps
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.CheckboxWidget
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget
import net.minecraft.screen.ScreenTexts
import opekope2.optigui.config.IConfig
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.gui.widget.ErrorListWidget
import opekope2.optigui.internal.I18n
import opekope2.optigui.util.ResourceLoadingLoggingEvent
import opekope2.optigui.util.ResourcePackIconLoader
import org.slf4j.event.Level

/**
 * A screen that shows a list of resource loading log entries.
 *
 * @param log The resource loading log
 * @param onClose The action that gets called when this screen is closed
 */
class ResourceLoadingErrorScreen(private val log: List<ResourceLoadingLoggingEvent>, private val onClose: Runnable) :
    Screen(I18n.OPTIGUI_RP_LOADER_LOAD_FAIL.getText()) {
    private val layout = ThreePartsLayoutWidget(this, ThreePartsLayoutWidget.DEFAULT_HEADER_FOOTER_HEIGHT, 58)
    private lateinit var errorsWidget: ErrorListWidget
    private lateinit var doNotShowAgainCheckbox: CheckboxWidget
    private lateinit var copyButton: ButtonWidget
    private lateinit var doneButton: ButtonWidget

    private lateinit var iconLoader: ResourcePackIconLoader

    private fun encodeErrors(errors: List<ResourceLoadingLoggingEvent>): Dynamic<*> {
        val result = buildMap {
            for (error in errors) {
                getOrPut(
                    error.packName ?: I18n.OPTIGUI_RP_LOADER_UNKNOWN_PACK.getTranslation(),
                    ::mutableMapOf
                ).getOrPut(
                    error.resourceId?.toString() ?: I18n.OPTIGUI_RP_LOADER_UNKNOWN_RESOURCE.getTranslation(),
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
        client!!.keyboard.clipboard = GSON.toJson(encodeErrors(errorsWidget.errors).convert(JsonOps.INSTANCE).value)
    }

    override fun init() {
        if (::iconLoader.isInitialized) iconLoader.close()
        iconLoader = ResourcePackIconLoader("resource_loading_error_screen", client!!.textureManager)

        layout.addHeader(title, textRenderer)

        errorsWidget = layout.addBody(
            ErrorListWidget(
                textRenderer,
                client!!.resourcePackManager,
                iconLoader,
                log,
                0,
                layout.headerHeight,
                width - 40,
                layout.contentHeight
            )
        )

        val footer = layout.addFooter(DirectionalLayoutWidget.vertical().spacing(5))
        footer.mainPositioner.alignHorizontalCenter()
        doNotShowAgainCheckbox = footer.add(
            CheckboxWidget.builder(I18n.OPTIGUI_RP_LOADER_DO_NOT_SHOW_AGAIN.getText(), textRenderer)
                .tooltip(Tooltip.of(I18n.OPTIGUI_RP_LOADER_DO_NOT_SHOW_AGAIN_TOOLTIP.getText()))
                .build()
        )

        val buttonBar = footer.add(DirectionalLayoutWidget.horizontal().spacing(5))
        copyButton = buttonBar.add(
            ButtonWidget.builder(I18n.OPTIGUI_RP_LOADER_COPY_TO_CLIPBOARD.getText()) { copyErrors() }
                .build()
        )
        doneButton = buttonBar.add(
            ButtonWidget.builder(ScreenTexts.DONE) { close() }
                .build()
        )

        layout.forEachChild(::addDrawableChild)
        initTabNavigation()
    }

    override fun removed() {
        if (::iconLoader.isInitialized) iconLoader.close()
        if (doNotShowAgainCheckbox.isChecked) {
            IConfig.get().showResourceLoadingErrors = IConfig.ResourceLoadingErrorFilter.NOTHING
            IConfig.get().save()
        }
    }

    override fun initTabNavigation() {
        layout.refreshPositions()
    }

    override fun close() {
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
        fun setScreen(screen: Screen?) = Runnable { MinecraftClient.getInstance().setScreen(screen) }

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
        fun shouldShow(): Boolean = when (IConfig.get().showResourceLoadingErrors) {
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
            val client = MinecraftClient.getInstance()
            client.setScreen(create(setScreen(client.currentScreen)))
        }
    }
}

