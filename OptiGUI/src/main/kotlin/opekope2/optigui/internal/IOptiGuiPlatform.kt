package opekope2.optigui.internal

internal interface IOptiGuiPlatform {
    val version: String

    fun isModInstalled(modId: String): Boolean

    companion object Instance : IOptiGuiPlatform by optiGuiPlatform
}
