package opekope2.optigui.internal.fabric

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener

internal class FabricResourceReloadListener(
    private val id: ResourceLocation,
    private val delegate: PreparableReloadListener
) : IdentifiableResourceReloadListener, PreparableReloadListener by delegate {
    override fun getFabricId() = id

    override fun getName() = "${javaClass.simpleName}[${delegate.name}]"
}
