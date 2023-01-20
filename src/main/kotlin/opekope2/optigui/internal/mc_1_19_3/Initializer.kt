package opekope2.optigui.internal.mc_1_19_3

import opekope2.optigui.provider.IRegistryLookupProvider
import opekope2.optigui.provider.IResourceManagerProvider
import opekope2.optigui.provider.registerProvider

internal fun initialize() {
    registerProvider<IResourceManagerProvider>(ResourceManagerProvider())
    registerProvider<IRegistryLookupProvider>(RegistryLookupProvider())
}
