package opekope2.optigui.internal.fabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.world.ClientWorld
import net.minecraft.resource.ResourceType
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.IOptiGuiPlatform
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.internal.initializer.ClientInitializer
import opekope2.optigui.internal.resource.loader.json.JsonFilterLoader
import opekope2.optigui.resource.format.json.ILoadTimeNbtSupplier
import opekope2.optigui.screen.ITextureChangeableScreen
import opekope2.optigui.util.MOD_ID
import kotlin.jvm.optionals.getOrNull

internal class OptiGuiClient :
    ClientModInitializer,
    ClientTickEvents.EndWorldTick,
    ClientPlayConnectionEvents.Disconnect,
    ScreenEvents.AfterInit,
    ScreenEvents.BeforeRender,
    ScreenEvents.AfterRender {
    override fun onInitializeClient() {
        ClientInitializer
        FabricInteractionHandler
        registerLoadTimeNbtSuppliers()
        registerResourceLoaders(ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES))
        ClientTickEvents.END_WORLD_TICK.register(this)
        ClientPlayConnectionEvents.DISCONNECT.register(this)
        ScreenEvents.AFTER_INIT.register(this)
    }

    private fun registerLoadTimeNbtSuppliers() {
        ILoadTimeNbtSupplier.register("mods", FabricModsNbtSupplier)
    }

    private fun registerResourceLoaders(manager: ResourceManagerHelper) {
        manager.registerReloadListener(FabricResourceReloadListener(JsonFilterLoader.ID, JsonFilterLoader))
        manager.registerReloadListener(TextureChangerReloadListener)
    }

    override fun onEndTick(world: ClientWorld?) {
        if (!InteractionManager.isInteracting) return
        InteractionManager.clearCache()
    }

    override fun onPlayDisconnect(handler: ClientPlayNetworkHandler?, client: MinecraftClient?) {
        InteractionManager.end(disconnected = true)
    }

    override fun afterInit(client: MinecraftClient?, screen: Screen?, scaledWidth: Int, scaledHeight: Int) {
        ScreenEvents.beforeRender(screen).register(this)
        ScreenEvents.afterRender(screen).register(this)

        if (screen is ITextureChangeableScreen) {
            val widget = FabricInspectorWidget()
            Screens.getButtons(screen).add(widget)
            ScreenEvents.beforeRender(screen).register(widget)
        }
    }

    override fun beforeRender(screen: Screen?, drawContext: DrawContext?, mouseX: Int, mouseY: Int, tickDelta: Float) {
        TextureChanger.renderingScreen = true
    }

    override fun afterRender(screen: Screen?, drawContext: DrawContext?, mouseX: Int, mouseY: Int, tickDelta: Float) {
        TextureChanger.renderingScreen = false
    }

    private object TextureChangerReloadListener : IdentifiableResourceReloadListener,
        SynchronousResourceReloader by TextureChanger {
        override fun getFabricId(): Identifier = Identifier.of(MOD_ID, "texture_changer")

        override fun getFabricDependencies() = IFilterLoader.map { it.key }
    }

    internal object Platform : IOptiGuiPlatform {
        override val version =
            FabricLoader.getInstance().getModContainer(MOD_ID).getOrNull()?.metadata?.version.toString()
    }
}
