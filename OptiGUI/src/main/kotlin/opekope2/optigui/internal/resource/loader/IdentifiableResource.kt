package opekope2.optigui.internal.resource.loader

import net.minecraft.util.Identifier

internal data class IdentifiableResource<TResource>(val packId: String, val id: Identifier, val resource: TResource) {
    @Suppress("UNCHECKED_CAST") // If resource is this.resource and resource is T, then this.resource must be T
    fun <T> withResource(resource: T): IdentifiableResource<T> =
        if (this.resource === resource) this as IdentifiableResource<T>
        else IdentifiableResource(packId, id, resource)
}
