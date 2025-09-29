package opekope2.optigui.filter.texture_changer

import net.minecraft.util.Identifier
import java.util.function.UnaryOperator

/**
 * Functional interface for changing a texture.
 */
fun interface ITextureChanger : UnaryOperator<Identifier>
