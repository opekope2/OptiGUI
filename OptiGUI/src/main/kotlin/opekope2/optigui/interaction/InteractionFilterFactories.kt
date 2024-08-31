package opekope2.optigui.interaction

import opekope2.optigui.registry.RegistryBase

/**
 * Interaction filter registry.
 */
object InteractionFilterFactories : RegistryBase<String, IInteractionFilterFactory>() {
    override fun validateEntry(key: String, value: IInteractionFilterFactory) {
        super.validateEntry(key, value)
        if (key == "if" || key.startsWith("if.")) {
            throw IllegalArgumentException("Key can't start with `if.`")
        }
    }
}
