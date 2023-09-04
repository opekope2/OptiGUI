package opekope2.optigui.properties

/**
 * Properties for llamas.
 *
 * @see net.minecraft.entity.passive.LlamaEntity
 * @see opekope2.optigui.properties.impl.LlamaProperties
 */
interface ILlamaProperties : IDonkeyProperties {
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
}
