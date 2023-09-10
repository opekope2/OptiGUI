package opekope2.optigui.api

import opekope2.optigui.impl.OptiGuiApi

@Suppress("unused", "ClassName")
private object `IOptiGuiApi$Instance` {
    @JvmStatic
    fun get(): IOptiGuiApi = OptiGuiApi
}
