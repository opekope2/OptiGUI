package opekope2.optigui.internal.ui

import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.CheckboxComponent
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.ParentComponent
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.config.ResourceLoadingLogFilter
import opekope2.optigui.config.config
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.internal.I18n
import opekope2.optigui.util.*
import org.slf4j.event.Level
import java.util.*

internal class ResourceLoadingLogScreen(
    private val log: List<ResourceLoadingLoggingEvent>,
    private val onClose: Runnable
) : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, MODEL_ID) {
    private val rootComponent get() = uiAdapter.rootComponent
    private val content by requireChildById<FlowLayout>(::rootComponent)
    private val doNotShowAgain by requireChildById<CheckboxComponent>(::rootComponent)
    private val ok by requireChildById<ButtonComponent>(::rootComponent)

    private var iconLoader: ResourcePackIconLoader? = null

    constructor(log: List<ResourceLoadingLoggingEvent>, parent: Screen?) : this(log, { mc.setScreen(parent) })
    constructor(onClose: Runnable) : this(IFilterLoader.lastResourceReloadLog, onClose)
    constructor(parent: Screen?) : this({ mc.setScreen(parent) })

    override fun build(rootComponent: FlowLayout) {
        addLog()
        ok.onPress { onClose() }
    }

    override fun init() { // called twice for some reason
        if (iconLoader == null) // initialize before super call, because it is needed in addLog()
            iconLoader = ResourcePackIconLoader("resource_loading_log_screen", minecraft!!.textureManager)
        super.init()
    }

    override fun removed() {
        super.removed()

        if (doNotShowAgain.selected()) config.showResourceLoadingErrors(ResourceLoadingLogFilter.NOTHING)

        iconLoader?.close()
        iconLoader = null
    }

    private fun addLog() {
        val packManager = minecraft!!.resourcePackRepository

        val log = log
            .filterTo(mutableListOf()) { it.level <= Level.INFO }
            .apply { sort() }
        var lastPack: String? = null
        var lastResource: ResourceLocation? = null
        var addHeaders = true

        for ((message, level, packName, resourceId) in log) {
            if (addHeaders || lastPack != packName) {
                lastPack = packName
                addHeaders = true // Always add resource header after resource pack header

                val pack = packName?.let(packManager::getPack)
                val icon = pack?.let(iconLoader!!::loadIcon) ?: ResourcePackIconLoader.UNKNOWN_PACK

                val packHeader by model.expandTemplate<ParentComponent>(mapOf("icon" to icon.toString()))
                val title by requireChildById<LabelComponent> { packHeader }
                val description by requireChildById<LabelComponent> { packHeader }

                content.child(packHeader)
                if (pack != null) {
                    title.text(pack.title)
                    description.text(pack.description)
                }
            }

            if (addHeaders || lastResource != resourceId) {
                lastResource = resourceId

                val resourceId = resourceId?.toString() ?: I18n.OPTIGUI_GUI_RP_LOADER_UNKNOWN_RESOURCE.getTranslation()
                val resourceHeader by model.expandTemplate<Component>(mapOf("resourceId" to resourceId))
                content.child(resourceHeader)
            }

            val logEntry by model.expandTemplate<LabelComponent>(
                mapOf("text" to message, "color" to LEVEL_TO_COLOR.getValue(level))
            )

            content.child(logEntry)

            addHeaders = false
        }
    }

    override fun onClose() {
        onClose.run()
    }

    companion object {
        private val MODEL_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "resource_loading_log")
        private val LEVEL_TO_COLOR = mapOf(
            Level.ERROR to ChatFormatting.RED,
            Level.WARN to ChatFormatting.YELLOW,
            Level.INFO to ChatFormatting.WHITE,
        ).mapValues { (_, formatting) -> Color.ofFormatting(formatting).asHexString(true) }

        /**
         * Returns if the filter loaders registered in [IFilterLoader.Registry] have errors the user configured to see.
         */
        @JvmStatic
        fun shouldShow() = config.showResourceLoadingErrors().shouldShowResourceLoadingLog()

        /**
         * Shows a new [ResourceLoadingLogScreen] If errors occurred while reloading resources.
         */
        @JvmStatic
        fun showIfErrorsOccurred() {
            if (shouldShow()) mc.setScreen(ResourceLoadingLogScreen(mc.screen))
        }
    }
}
