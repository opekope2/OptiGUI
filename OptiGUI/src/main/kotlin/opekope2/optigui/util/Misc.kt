package opekope2.optigui.util

/**
 * Calls [action] if the receiver is null.
 *
 * @return The receiver
 */
inline infix fun <T : Any?> T.ifNull(action: () -> Unit): T {
    if (this == null) action()
    return this
}
