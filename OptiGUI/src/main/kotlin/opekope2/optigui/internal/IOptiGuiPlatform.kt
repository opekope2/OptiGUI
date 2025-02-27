package opekope2.optigui.internal

internal interface IOptiGuiPlatform {
    val version: String

    companion object Instance : IOptiGuiPlatform by optiGuiPlatform
}
