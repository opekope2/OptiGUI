package opekope2.optigui.gui.widget

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractScrollWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.locale.Language
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.repository.PackRepository
import net.minecraft.util.FormattedCharSequence
import opekope2.optigui.internal.I18n
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.ResourceLoadingLoggingEvent
import opekope2.optigui.util.ResourcePackIconLoader
import org.slf4j.event.Level

/**
 * A widget that shows errors and warnings grouped by resource packs and resources
 */
class ErrorListWidget(
    textRenderer: Font,
    packManager: PackRepository,
    iconLoader: ResourcePackIconLoader,
    log: List<ResourceLoadingLoggingEvent>,
    x: Int,
    y: Int,
    w: Int,
    h: Int
) : AbstractScrollWidget(x, y, w, h, I18n.OPTIGUI_NARRATION_RP_LOADER_ERROR_LIST.getText()) {
    val errors: List<ResourceLoadingLoggingEvent> = log
        .filterTo(mutableListOf()) { it.level == Level.ERROR || it.level == Level.WARN }
        .apply { sort() }
    private val entries = mutableListOf<Entry>()
    private val narration: MutableComponent
    private val contentHeight: Int

    init {
        val narration = mutableListOf<Component>(message)
        var contentHeight = PADDING - innerPadding()
        var lastPack: String? = null
        var lastRes: ResourceLocation? = null
        var lastResText: Component = I18n.OPTIGUI_GUI_RP_LOADER_UNKNOWN_RESOURCE.getText()
        var addHeaders = true
        val entryWidth = width - PADDING - PADDING

        for (error in errors) {
            if (error.level != Level.ERROR && error.level != Level.WARN) continue

            if (addHeaders || lastPack != error.packName) {
                lastPack = error.packName
                addHeaders = true // Always add resource header after resource pack header

                val pack = if (error.packName == null) null else packManager.getPack(error.packName)
                val title = pack?.title ?: I18n.OPTIGUI_GUI_RP_LOADER_UNKNOWN_PACK.getText()
                val description = pack?.description ?: Component.empty()
                val icon = pack?.let(iconLoader::loadIcon) ?: ResourcePackIconLoader.UNKNOWN_PACK
                val entry = ResourcePackEntry(title, description, icon, entryWidth, textRenderer)

                entries.add(entry)
                narration += I18n.OPTIGUI_NARRATION_RP_LOADER_RESOURCE_PACK.getText(title)
                contentHeight += entry.height + PADDING
            }

            if (addHeaders || lastRes != error.resourceId) {
                lastRes = error.resourceId
                lastResText = error.resourceId?.toString()?.let(Component::literal)
                    ?: I18n.OPTIGUI_GUI_RP_LOADER_UNKNOWN_RESOURCE.getText()
                val entry = ResourceEntry(lastResText, entryWidth, textRenderer)

                entries.add(entry)
                contentHeight += entry.height + PADDING
            }

            val entry = ErrorEntry(error.level, error.message, entryWidth, textRenderer)

            entries.add(entry)
            narration += getNarration(error.level).getText(lastResText, error.message)
            contentHeight += entry.height + PADDING

            addHeaders = false
        }

        this.narration = ComponentUtils.formatList(narration, CommonComponents.NARRATION_SEPARATOR).copy()
        this.contentHeight = contentHeight - innerPadding()
    }

    override fun getInnerHeight() = contentHeight

    override fun scrollRate() = 9.0

    override fun renderContents(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val xx = x + PADDING
        var yy = y + PADDING

        for (entry in entries) {
            entry.render(context, xx, yy)
            yy += entry.height + PADDING
        }
    }

    // FIXME stitching together a colossal narration text is probably not the best way to implement narration, but EntryListWidget doesn't allow variable height entries
    // TODO Identifier and resource pack narration (colon, hyphen, forward slash, dot)
    override fun createNarrationMessage() = narration

    override fun updateWidgetNarration(builder: NarrationElementOutput) {
        builder.add(NarratedElementType.TITLE, createNarrationMessage())
    }

    private sealed class Entry(protected val textRenderer: Font) {
        abstract val height: Int

        abstract fun render(gfx: GuiGraphics, x: Int, y: Int)
    }

    private class ResourcePackEntry(
        title: Component,
        description: Component,
        private val icon: ResourceLocation,
        width: Int,
        textRenderer: Font
    ) : Entry(textRenderer) {
        private val textWidth = width - PACK_INDENT
        private val title =
            if (textRenderer.width(title) <= textWidth) title.visualOrderText
            else Language.getInstance().getVisualOrder(
                FormattedText.composite(
                    textRenderer.substrByWidth(title, textWidth - textRenderer.width(ELLIPSIS)),
                    FormattedText.of(ELLIPSIS)
                )
            )
        private val description: List<FormattedCharSequence> = textRenderer.split(description, textWidth)

        override val height: Int = ((1 + this.description.size) * textRenderer.lineHeight).coerceAtLeast(PACK_ICON_SIZE)

        override fun render(gfx: GuiGraphics, x: Int, y: Int) {
            val xx = x + PACK_ICON_SIZE + PADDING
            var yy = y

            gfx.blit(icon, x, yy, 0f, 0f, PACK_ICON_SIZE, PACK_ICON_SIZE, PACK_ICON_SIZE, PACK_ICON_SIZE)
            gfx.drawString(textRenderer, title, xx, yy, WHITE, true)

            yy += textRenderer.lineHeight
            for (line in description) {
                gfx.drawString(textRenderer, line, xx, yy, GRAY, true)
                yy += textRenderer.lineHeight
            }
        }
    }

    private class ResourceEntry(text: Component, width: Int, textRenderer: Font) : Entry(textRenderer) {
        private val textWidth = width - PACK_INDENT
        private val lines = textRenderer.split(text, textWidth)

        override val height: Int = (lines.size * textRenderer.lineHeight).coerceAtLeast(ERROR_ICON_SIZE)

        override fun render(gfx: GuiGraphics, x: Int, y: Int) {
            val xx = x + PACK_INDENT
            var yy = y

            if (lines.size == 1) yy += (height - textRenderer.lineHeight + 1) / 2
            for (line in lines) {
                gfx.drawString(textRenderer, line, xx, yy, WHITE, true)
                yy += textRenderer.lineHeight
            }
        }
    }

    private class ErrorEntry(logLevel: Level, message: String, width: Int, textRenderer: Font) :
        Entry(textRenderer) {
        private val textWidth = width - PACK_INDENT - ERROR_INDENT
        private val lines = textRenderer.split(FormattedText.of(message), textWidth)
        private val sprite = getSprite(logLevel)

        override val height: Int = (lines.size * textRenderer.lineHeight).coerceAtLeast(ERROR_ICON_SIZE)

        override fun render(gfx: GuiGraphics, x: Int, y: Int) {
            var xx = x + PACK_INDENT
            var yy = y

            gfx.blitSprite(sprite, xx, yy, ERROR_ICON_SIZE, ERROR_ICON_SIZE)

            xx += ERROR_INDENT
            if (lines.size == 1) yy += (height - textRenderer.lineHeight + 1) / 2
            for (line in lines) {
                gfx.drawString(textRenderer, line, xx, yy, WHITE, true)
                yy += textRenderer.lineHeight
            }
        }
    }

    private companion object {
        private const val ERROR_ICON_SIZE = 16
        private const val PACK_ICON_SIZE = 32
        private const val PADDING = 4
        private const val WHITE = 0xFFFFFFFF.toInt()
        private const val GRAY = 0xFF808080.toInt()
        private const val PACK_INDENT = PACK_ICON_SIZE + PADDING
        private const val ERROR_INDENT = ERROR_ICON_SIZE + PADDING
        private const val ELLIPSIS = "..."

        private val ERROR_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "icon/error")
        private val WARNING_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "icon/warning")

        private fun getSprite(logLevel: Level) = when (logLevel) {
            Level.ERROR -> ERROR_SPRITE
            Level.WARN -> WARNING_SPRITE
            else -> throw IllegalArgumentException("Unsupported error level $logLevel")
        }

        private fun getNarration(logLevel: Level) = when (logLevel) {
            Level.ERROR -> I18n.OPTIGUI_NARRATION_RP_LOADER_ERROR
            Level.WARN -> I18n.OPTIGUI_NARRATION_RP_LOADER_WARNING
            else -> throw IllegalArgumentException("Unsupported error level $logLevel")
        }
    }
}

