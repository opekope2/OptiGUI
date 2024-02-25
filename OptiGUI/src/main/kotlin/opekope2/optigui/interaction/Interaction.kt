package opekope2.optigui.interaction

import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * Interaction representation with a block entity or entity.
 *
 * @param texture The texture to be replaced.
 * @param screenTitle The active GUI screen's title
 * @param rawInteraction The raw interaction from Minecraft containing the details. See [RawInteraction] documentation
 * @param data The interaction data returned by the interaction data provider
 *
 * @see IBlockEntityProcessor
 * @see IEntityProcessor
 */
data class Interaction(
    val texture: Identifier,
    val screenTitle: Text,
    val rawInteraction: RawInteraction?,
    val data: Any?
)
