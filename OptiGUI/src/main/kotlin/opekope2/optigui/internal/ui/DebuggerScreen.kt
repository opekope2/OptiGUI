package opekope2.optigui.internal.ui

import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TextComponentTagVisitor
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.mixin.TextComponentTagVisitorAccessor
import opekope2.optigui.util.MOD_ID
import opekope2.optigui.util.NbtFilterEvaluation
import opekope2.optigui.util.childById
import opekope2.optigui.util.collections.LinkedMruCollection
import opekope2.optigui.util.expandTemplate

internal class DebuggerScreen(private val parent: Screen?, private val interaction: IInteraction?) :
    BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, MODEL_ID) {
    private val rootComponent get() = uiAdapter.rootComponent
    private val content by childById<FlowLayout>(::rootComponent)
    private val done by childById<ButtonComponent>(::rootComponent)

    override fun build(rootComponent: FlowLayout) {
        if (interaction != null) addDebugData(interaction)
        done.onPress { onClose() }
    }

    private fun addDebugData(interaction: IInteraction) {
        val filters = TextureChanger.filters.getOrDefault(interaction.target, EMPTY_FILTERS)
        val nbt = interaction.createNbt()

        for (filter in filters) {
            val title = Component.literal(filter.resourceId.toString())
                .withStyle(if (filter.test(nbt, nbt)) ChatFormatting.GREEN else ChatFormatting.RED)
                .withInputNbtTooltip(nbt)

            Containers.collapsible(Sizing.fill(), Sizing.content(), title, filters.size == 1)
                .child(FilterDebugComponent(NbtFilterEvaluation(filter, nbt, nbt), true)).let(content::child)
        }

        if (filters.isEmpty()) {
            val noFilters by model.expandTemplate<LabelComponent>(mapOf())
            content.child(noFilters)
        }
    }

    override fun onClose() {
        minecraft!!.setScreen(parent)
    }

    private class FilterDebugComponent(evaluation: NbtFilterEvaluation, expanded: Boolean) :
        CollapsibleContainer(Sizing.fill(), Sizing.content(), evaluation.createTitle(), expanded) {
        private val subFilters = evaluation.testSubFilters()
        private var childrenAdded = false

        init {
            if (subFilters.isEmpty()) titleLayout.removeChild(spinnyBoi)
            if (expanded) addChildren()
        }

        override fun toggleExpansion() {
            if (!childrenAdded) addChildren()
            super.toggleExpansion()
        }

        private fun addChildren() {
            children(subFilters.map { FilterDebugComponent(it, subFilters.size == 1) })
            childrenAdded = true
        }
    }

    class InputNbtVisitor : TextComponentTagVisitor("") {
        init {
            @Suppress("CAST_NEVER_SUCCEEDS") // Mixin
            (this as IAccessor).optiGui_setDepth(TextComponentTagVisitorAccessor.optiGui_getMaxDepth() - 1)
        }

        @Suppress("FunctionName") // Mixin
        interface IAccessor {
            fun optiGui_setDepth(depth: Int)
        }
    }

    private companion object {
        private val MODEL_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "debugger")
        private val EMPTY_FILTERS = LinkedMruCollection<TextureChangerFilter>(emptyList())

        private fun NbtFilterEvaluation.createTitle(): MutableComponent {
            val title = Component.literal(filter.asString())
                .withStyle(if (test()) ChatFormatting.GREEN else ChatFormatting.RED)

            return if (nbt == null) title else title.withInputNbtTooltip(nbt)
        }

        private fun MutableComponent.withInputNbtTooltip(input: Tag): MutableComponent {
            val tooltip = InputNbtVisitor().visit(input)
            return withStyle(Style.EMPTY.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)))
        }
    }
}
