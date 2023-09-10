package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for llamas.
 *
 * @see net.minecraft.entity.passive.LlamaEntity
 * @see opekope2.optigui.properties.impl.LlamaProperties
 */
interface ILlamaProperties : IDonkeyProperties, IInteractionData {
    /**
     * The color of the carpet on the llama or `null`, if there is no carpet on the llama.
     *
     * @see net.minecraft.entity.passive.LlamaEntity.getCarpetColor
     */
    val carpetColor: String?

    /**
     * The variant of the llama.
     *
     * @see net.minecraft.entity.passive.LlamaEntity.getVariant
     */
    val variant: String

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super.writeSelectors(appendSelector)
        carpetColor?.let { carpetColor -> appendSelector.accept("llama.colors", carpetColor) }
        appendSelector.accept("llama.variants", variant)
    }
}
