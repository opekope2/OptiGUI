package opekope2.optigui.interaction

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

/**
 * Callback fired before an interaction begins.
 */
fun interface IBeforeInteractionBeginCallback {
    /**
     * Called right before an interaction begins. This is the last place when calling [Interaction.prepare] is valid.
     */
    fun onBeforeBegin()

    companion object{
        /**
         * Event fired before an interaction begins.
         */
        @JvmField
        val EVENT: Event<IBeforeInteractionBeginCallback> =
            EventFactory.createArrayBacked(IBeforeInteractionBeginCallback::class.java) { listeners ->
                IBeforeInteractionBeginCallback {
                    listeners.forEach(IBeforeInteractionBeginCallback::onBeforeBegin)
                }
            }
    }
}
