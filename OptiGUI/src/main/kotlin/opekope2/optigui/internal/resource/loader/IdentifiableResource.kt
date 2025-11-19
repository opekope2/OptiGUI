package opekope2.optigui.internal.resource.loader

import net.minecraft.resources.ResourceLocation

internal data class IdentifiableResource<T>(val packId: String, val id: ResourceLocation, val resource: T) {
    @Suppress("UNCHECKED_CAST") // If resource is this.resource and resource is TNew, then this.resource must be TNew
    fun <TNew> withResource(resource: TNew) =
        if (this.resource === resource) this as IdentifiableResource<TNew>
        else IdentifiableResource(packId, id, resource)
}
