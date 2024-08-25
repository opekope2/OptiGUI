package opekope2.optigui.screen

interface IRedstoneComparatorOutputGetterScreen {
    @Suppress("FunctionName") // Mixin
    fun optiGUI_getRedstoneComparatorOutput(): Int

    val redstoneComparatorOutput: Int
        get() = optiGUI_getRedstoneComparatorOutput()
}
