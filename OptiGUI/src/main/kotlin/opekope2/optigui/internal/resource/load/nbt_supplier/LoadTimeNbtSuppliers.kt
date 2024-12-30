package opekope2.optigui.internal.resource.load.nbt_supplier

import opekope2.optigui.resource.load.ILoadTimeNbtSupplier

internal fun register() {
    ILoadTimeNbtSupplier.register("mods", ModListsNbtSupplier)
}
