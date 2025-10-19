package opekope2.optigui.gui.widget

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.ScrollableWidget
import net.minecraft.resource.ResourcePackManager
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.*
import net.minecraft.util.Identifier
import net.minecraft.util.Language
import opekope2.optigui.internal.I18n
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.ResourceLoadingLoggingEvent
import opekope2.optigui.util.ResourcePackIconLoader
import org.slf4j.event.Level

/**
 * A widget that shows errors and warnings grouped by resource packs and resources
 */
class ErrorListWidget(
    textRenderer: TextRenderer,
    packManager: ResourcePackManager,
    iconLoader: ResourcePackIconLoader,
    log: List<ResourceLoadingLoggingEvent>,
    x: Int,
    y: Int,
    w: Int,
    h: Int
) : ScrollableWidget(x, y, w, h, I18n.OPTIGUI_RP_LOADER_NARRATION_ERROR_LIST.getText()) {
    val errors: List<ResourceLoadingLoggingEvent> = log
        .filterTo(mutableListOf()) { it.level == Level.ERROR || it.level == Level.WARN }
        .apply { sort() }
    private val entries = mutableListOf<Entry>()
    private val narration: MutableText
    private val contentHeight: Int

    init {
        val narration = mutableListOf<Text>(message)
        var contentHeight = PADDING - padding
        var lastPack: String? = null
        var lastRes: Identifier? = null
        var lastResText: Text = I18n.OPTIGUI_RP_LOADER_UNKNOWN_RESOURCE.getText()
        var addHeaders = true
        val entryWidth = width - PADDING - PADDING

        for (error in errors) {
            if (error.level != Level.ERROR && error.level != Level.WARN) continue

            if (addHeaders || lastPack != error.packName) {
                lastPack = error.packName
                addHeaders = true // Always add resource header after resource pack header

                val pack = if (error.packName == null) null else packManager.getProfile(error.packName)
                val title = pack?.displayName ?: I18n.OPTIGUI_RP_LOADER_UNKNOWN_PACK.getText()
                val description = pack?.description ?: Text.empty()
                val icon = pack?.let(iconLoader::loadIcon) ?: ResourcePackIconLoader.UNKNOWN_PACK
                val entry = ResourcePackEntry(title, description, icon, entryWidth, textRenderer)

                entries.add(entry)
                narration += I18n.OPTIGUI_RP_LOADER_NARRATION_RESOURCE_PACK.getText(title)
                contentHeight += entry.height + PADDING
            }

            if (addHeaders || lastRes != error.resourceId) {
                lastRes = error.resourceId
                lastResText = error.resourceId?.toString()?.let(Text::literal)
                    ?: I18n.OPTIGUI_RP_LOADER_UNKNOWN_RESOURCE.getText()
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

        this.narration = Texts.join(narration, ScreenTexts.SENTENCE_SEPARATOR).copy()
        this.contentHeight = contentHeight - padding
    }

    override fun getContentsHeight() = contentHeight

    override fun getDeltaYPerScroll() = 9.0

    override fun renderContents(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val xx = x + PADDING
        var yy = y + PADDING

        for (entry in entries) {
            entry.render(context, xx, yy)
            yy += entry.height + PADDING
        }
    }

    // FIXME stitching together a colossal narration text is probably not the best way to implement narration, but EntryListWidget doesn't allow variable height entries
    // TODO Identifier and resource pack narration (colon, hyphen, forward slash, dot)
    override fun getNarrationMessage() = narration

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        builder.put(NarrationPart.TITLE, narrationMessage)
    }

    private sealed class Entry(protected val textRenderer: TextRenderer) {
        abstract val height: Int

        abstract fun render(ctx: DrawContext, x: Int, y: Int)
    }

    private class ResourcePackEntry(
        title: Text,
        description: Text,
        private val icon: Identifier,
        width: Int,
        textRenderer: TextRenderer
    ) : Entry(textRenderer) {
        private val textWidth = width - PACK_INDENT
        private val title =
            if (textRenderer.getWidth(title) <= textWidth) title.asOrderedText()
            else Language.getInstance().reorder(
                StringVisitable.concat(
                    textRenderer.trimToWidth(title, textWidth - textRenderer.getWidth(ELLIPSIS)),
                    StringVisitable.plain(ELLIPSIS)
                )
            )
        private val description: List<OrderedText> = textRenderer.wrapLines(description, textWidth)

        override val height: Int = ((1 + this.description.size) * textRenderer.fontHeight).coerceAtLeast(PACK_ICON_SIZE)

        override fun render(ctx: DrawContext, x: Int, y: Int) {
            val xx = x + PACK_ICON_SIZE + PADDING
            var yy = y

            ctx.drawTexture(icon, x, yy, 0f, 0f, PACK_ICON_SIZE, PACK_ICON_SIZE, PACK_ICON_SIZE, PACK_ICON_SIZE)
            ctx.drawText(textRenderer, title, xx, yy, WHITE, true)

            yy += textRenderer.fontHeight
            for (line in description) {
                ctx.drawText(textRenderer, line, xx, yy, GRAY, true)
                yy += textRenderer.fontHeight
            }
        }
    }

    private class ResourceEntry(text: Text, width: Int, textRenderer: TextRenderer) : Entry(textRenderer) {
        private val textWidth = width - PACK_INDENT
        private val lines = textRenderer.wrapLines(text, textWidth)

        override val height: Int = (lines.size * textRenderer.fontHeight).coerceAtLeast(ERROR_ICON_SIZE)

        override fun render(ctx: DrawContext, x: Int, y: Int) {
            val xx = x + PACK_INDENT
            var yy = y

            if (lines.size == 1) yy += (height - textRenderer.fontHeight + 1) / 2
            for (line in lines) {
                ctx.drawText(textRenderer, line, xx, yy, WHITE, true)
                yy += textRenderer.fontHeight
            }
        }
    }

    private class ErrorEntry(logLevel: Level, message: String, width: Int, textRenderer: TextRenderer) :
        Entry(textRenderer) {
        private val textWidth = width - PACK_INDENT - ERROR_INDENT
        private val lines = textRenderer.wrapLines(StringVisitable.plain(message), textWidth)
        private val sprite = getSprite(logLevel)

        override val height: Int = (lines.size * textRenderer.fontHeight).coerceAtLeast(ERROR_ICON_SIZE)

        override fun render(ctx: DrawContext, x: Int, y: Int) {
            var xx = x + PACK_INDENT
            var yy = y

            ctx.drawGuiTexture(sprite, xx, yy, ERROR_ICON_SIZE, ERROR_ICON_SIZE)

            xx += ERROR_INDENT
            if (lines.size == 1) yy += (height - textRenderer.fontHeight + 1) / 2
            for (line in lines) {
                ctx.drawText(textRenderer, line, xx, yy, WHITE, true)
                yy += textRenderer.fontHeight
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

        private val ERROR_SPRITE = Identifier.of(MOD_ID, "icon/error")
        private val WARNING_SPRITE = Identifier.of(MOD_ID, "icon/warning")

        private fun getSprite(logLevel: Level) = when (logLevel) {
            Level.ERROR -> ERROR_SPRITE
            Level.WARN -> WARNING_SPRITE
            else -> throw IllegalArgumentException("Unsupported error level $logLevel")
        }

        private fun getNarration(logLevel: Level) = when (logLevel) {
            Level.ERROR -> I18n.OPTIGUI_RP_LOADER_NARRATION_ERROR
            Level.WARN -> I18n.OPTIGUI_RP_LOADER_NARRATION_WARNING
            else -> throw IllegalArgumentException("Unsupported error level $logLevel")
        }
    }
}

