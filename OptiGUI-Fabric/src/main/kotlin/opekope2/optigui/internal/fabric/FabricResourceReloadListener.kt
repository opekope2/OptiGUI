package opekope2.optigui.internal.fabric

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import java.util.function.Supplier

internal class FabricResourceReloadListener(
    private val id: ResourceLocation,
    private val delegate: PreparableReloadListener,
    val dependencySupplier: Supplier<Collection<ResourceLocation>>,
) : IdentifiableResourceReloadListener, PreparableReloadListener by delegate {
    override fun getFabricId() = id

    override fun getFabricDependencies() = dependencySupplier.get()

    override fun getName() = "${javaClass.simpleName}[${delegate.name}]"
}
