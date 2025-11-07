package opekope2.optigui.internal.fabric

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier

internal class FabricResourceReloadListener(private val id: Identifier, private val delegate: ResourceReloader) :
    IdentifiableResourceReloadListener, ResourceReloader by delegate {
    override fun getFabricId() = id

    override fun getName(): String = "${javaClass.simpleName}[${delegate.name}]"
}
