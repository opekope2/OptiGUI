package opekope2.optigui.interaction

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.screen.Screen

/**
 * Callback fired before an interaction begins.
 */
fun interface IBeforeInteractionBeginCallback {
    /**
     * Called right before an interaction begins. This is the last place when calling [Interaction.prepare] is valid.
     *
     * @param screen The screen, which triggered the interaction and is about to be opened
     */
    fun onBeforeBegin(screen: Screen)

    companion object {
        /**
         * Event fired before an interaction begins.
         */
        @JvmField
        val EVENT: Event<IBeforeInteractionBeginCallback> =
            EventFactory.createArrayBacked(IBeforeInteractionBeginCallback::class.java) { listeners ->
                IBeforeInteractionBeginCallback { screen ->
                    listeners.forEach { listener ->
                        listener.onBeforeBegin(screen)
                    }
                }
            }
    }
}
