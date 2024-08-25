@file: JvmName("InteractionEvents")

package opekope2.optigui.interaction

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import opekope2.optigui.screen.IRetexturableScreen

/**
 * Callback fired before an interaction begins.
 */
fun interface IBeforeInteractionBeginCallback {
    /**
     * Called right before an interaction begins. This is the last place when calling [InteractionManager.prepare] is valid.
     *
     * @param screen The screen, which triggered the interaction and is about to be opened
     */
    fun onBeforeInteractionBegin(screen: IRetexturableScreen)
}

/**
 * Callback fired after an interaction ends.
 */
fun interface IAfterInteractionEndCallback {
    /**
     * Called right after an interaction ends.
     */
    fun onAfterInteractionEnd()
}

/**
 * Event fired before an interaction begins.
 */
@JvmField
val BEFORE_INTERACTION_BEGIN_EVENT: Event<IBeforeInteractionBeginCallback> =
    EventFactory.createArrayBacked(IBeforeInteractionBeginCallback::class.java) { listeners ->
        IBeforeInteractionBeginCallback { screen ->
            listeners.forEach { listener ->
                listener.onBeforeInteractionBegin(screen)
            }
        }
    }

/**
 * Event fired after an interaction ends.
 */
@JvmField
val AFTER_INTERACTION_END_EVENT: Event<IAfterInteractionEndCallback> =
    EventFactory.createArrayBacked(IAfterInteractionEndCallback::class.java) { listeners ->
        IAfterInteractionEndCallback {
            listeners.forEach { listener ->
                listener.onAfterInteractionEnd()
            }
        }
    }
