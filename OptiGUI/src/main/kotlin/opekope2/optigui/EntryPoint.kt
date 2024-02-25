package opekope2.optigui


/**
 * OptiGUI entry point. Called when OptiGUI finishes initialization.
 * This is where [InitializerContext.registerFilterFactory] should be called.
 */
fun interface EntryPoint {
    /**
     * Registers filter factories using [context].
     *
     * @param context The initialization context
     */
    fun onInitialize(context: InitializerContext)
}
