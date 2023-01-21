package opekope2.optigui

import  opekope2.optigui.interaction.registerPreprocessor
import  opekope2.filter.registerFilterFactory

/**
 * OptiGUI entry point. Called when OptiGUI finishes initialization.
 * This is where [registerFilterFactory] and [registerPreprocessor] should be called.
 */
fun interface EntryPoint {
    /**
     * Runs the entry point.
     */
    fun run()
}
