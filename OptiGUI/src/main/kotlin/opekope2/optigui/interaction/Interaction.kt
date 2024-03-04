package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import opekope2.optigui.internal.TextureReplacer

/**
 * Interaction representation with a block entity or entity.
 *
 * @param container The identifier of the interacted container. Same as in the `/summon` or `/setblock` commands
 * @param texture The texture to be replaced.
 * @param screen The active GUI screen
 * @param data The details of the interaction
 */
data class Interaction(
    val container: Identifier,
    val texture: Identifier,
    val screen: Screen,
    val data: Data
) {
    /**
     * Details about an interaction.
     *
     * @param player The interacting player
     * @param world The world the interaction happened in. Must be client world
     * @param hand The player's interacting hand
     * @param extra Extra properties to supply to the filters. Can be mutable
     * @param blockEntity The target block entity. Is `null`, if [entity] is not `null`
     * @param entity The target entity. Is `null`, if [blockEntity] is not `null`
     */
    data class Data(
        val player: PlayerEntity,
        val world: World,
        val hand: Hand,
        val hitResult: HitResult?,
        val extra: Any?,
        val blockEntity: BlockEntity?,
        val entity: Entity?
    ) {
        init {
            if (!world.isClient) throw IllegalArgumentException("World must be client world")
            if (blockEntity != null && entity != null) throw IllegalArgumentException("Can't set both blockEntity and entity")
        }

        /**
         * Gets [entity] if not `null`, or the entity [player] is sitting on. Preferred over [entity].
         */
        val entityOrRiddenEntity: Entity?
            get() = entity ?: player.vehicle
    }

    companion object {
        @JvmStatic
        private fun prepare(
            container: Identifier,
            player: PlayerEntity,
            world: World,
            hand: Hand,
            hitResult: HitResult?,
            extra: Any?,
            blockEntity: BlockEntity?,
            entity: Entity?
        ): Boolean = TextureReplacer.prepareInteraction(
            container,
            Data(player, world, hand, hitResult, extra, blockEntity, entity)
        )

        /**
         * Prepares OptiGUI texture replacer for an interaction. Must be called before a [Screen] is opened.
         * If called multiple times before a [Screen] is opened, the last call takes effect.
         *
         * @return `true` if a GUI is not open, otherwise `false`
         * @see Data
         * @see IBeforeInteractionBeginCallback
         */
        @JvmStatic
        fun prepare(
            container: Identifier,
            player: PlayerEntity,
            world: World,
            hand: Hand,
            hitResult: HitResult?,
            extra: Any?
        ): Boolean = prepare(container, player, world, hand, hitResult, extra, null, null)

        /**
         * Prepares OptiGUI texture replacer for an interaction. Must be called before a [Screen] is opened.
         * If called multiple times before a [Screen] is opened, the last call takes effect.
         *
         * @return `true` if a GUI is not open, otherwise `false`
         * @see Data
         * @see IBeforeInteractionBeginCallback
         */
        @JvmStatic
        fun prepare(
            container: Identifier,
            player: PlayerEntity,
            world: World,
            hand: Hand,
            hitResult: HitResult?,
            extra: Any?,
            blockEntity: BlockEntity
        ): Boolean = prepare(container, player, world, hand, hitResult, extra, blockEntity, null)

        /**
         * Prepares OptiGUI texture replacer for an interaction. Must be called before a [Screen] is opened.
         * If called multiple times before a [Screen] is opened, the last call takes effect.
         *
         * @return `true` if a GUI is not open, otherwise `false`
         * @see Data
         * @see IBeforeInteractionBeginCallback
         */
        @JvmStatic
        fun prepare(
            container: Identifier,
            player: PlayerEntity,
            world: World,
            hand: Hand,
            hitResult: HitResult?,
            extra: Any?,
            entity: Entity
        ): Boolean = prepare(container, player, world, hand, hitResult, extra, null, entity)
    }
}
