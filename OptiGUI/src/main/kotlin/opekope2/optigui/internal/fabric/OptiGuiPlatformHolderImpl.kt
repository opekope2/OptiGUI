@file:JvmName("OptiGuiPlatformHolderImpl")

package opekope2.optigui.internal.fabric

import opekope2.optigui.internal.IOptiGuiPlatform

internal val optiGuiPlatform: IOptiGuiPlatform
    get() = OptiGuiClient.Platform
