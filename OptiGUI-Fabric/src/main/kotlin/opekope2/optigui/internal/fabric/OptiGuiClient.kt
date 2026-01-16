package opekope2.optigui.internal.fabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import opekope2.optigui.config.config
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.AbstractOptiGuiClient
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.internal.fabric.event_handler.FabricAttackHandler
import opekope2.optigui.internal.fabric.event_handler.FabricInteractionHandler
import opekope2.optigui.internal.fabric.filter.NbtVersionFilter
import opekope2.optigui.internal.fabric.gui.widget.FabricInspectorWidget
import opekope2.optigui.internal.fabric.nbt_provider.FabricModsNbtProvider
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen
import opekope2.optigui.util.MOD_ID
import kotlin.jvm.optionals.getOrNull

internal class OptiGuiClient :
    AbstractOptiGuiClient(),
    ClientModInitializer,
    ClientTickEvents.EndWorldTick,
    ClientPlayConnectionEvents.Disconnect,
    ScreenEvents.AfterInit,
    ScreenEvents.BeforeRender,
    ScreenEvents.AfterRender {
    override val version = FabricLoader.getInstance().getModContainer(MOD_ID).getOrNull()?.metadata?.version.toString()

    override fun isModInstalled(modId: String) = FabricLoader.getInstance().isModLoaded(modId)

    override fun onInitializeClient() {
        super.initialize()

        registerResourceLoaders(ResourceManagerHelper.get(PackType.CLIENT_RESOURCES))
        FabricInteractionHandler.initialize()
        FabricAttackHandler.initialize()
        ClientTickEvents.END_WORLD_TICK.register(this)
        ClientPlayConnectionEvents.DISCONNECT.register(this)
        ScreenEvents.AFTER_INIT.register(this)
    }

    override fun registerNbtFilters() {
        super.registerNbtFilters()
        INbtFilter.register(">v", NbtVersionFilter.Type.VERSION_GREATER)
        INbtFilter.register(">=v", NbtVersionFilter.Type.VERSION_GREATER_EQUAL)
        INbtFilter.register("=v", NbtVersionFilter.Type.VERSION_EQUAL)
        INbtFilter.register("!=v", NbtVersionFilter.Type.VERSION_NOT_EQUAL)
        INbtFilter.register("<=v", NbtVersionFilter.Type.VERSION_LESS_EQUAL)
        INbtFilter.register("<v", NbtVersionFilter.Type.VERSION_LESS)
        INbtFilter.register("~v", NbtVersionFilter.Type.VERSION_SAME_TO_NEXT_MINOR)
        INbtFilter.register("^v", NbtVersionFilter.Type.VERSION_SAME_TO_NEXT_MAJOR)
    }

    override fun registerLoadTimeNbtProviders() {
        super.registerLoadTimeNbtProviders()
        ILoadTimeNbtProvider.register("mods", FabricModsNbtProvider)
    }

    private fun registerResourceLoaders(manager: ResourceManagerHelper) {
        val id = ResourceLocation.fromNamespaceAndPath(MOD_ID, "texture_changer")
        val textureChanger = FabricResourceReloadListener(id, TextureChanger) { IFilterLoader.map { it.key } }
        manager.registerReloadListener(textureChanger)

        for ((id, loader) in IFilterLoader) {
            manager.registerReloadListener(FabricResourceReloadListener(id, loader, ::emptyList))
        }
    }

    override fun onEndTick(world: ClientLevel?) {
        if (!InteractionManager.isInteracting) return
        InteractionManager.clearCache()
    }

    override fun onPlayDisconnect(handler: ClientPacketListener?, client: Minecraft?) {
        InteractionManager.end(disconnected = true)
    }

    override fun afterInit(client: Minecraft?, screen: Screen?, scaledWidth: Int, scaledHeight: Int) {
        ScreenEvents.beforeRender(screen).register(this)
        ScreenEvents.afterRender(screen).register(this)

        if (screen is ITextureChangeableScreen && config.enableInspector()) {
            val widget = FabricInspectorWidget()
            Screens.getButtons(screen).add(widget)
            ScreenEvents.beforeRender(screen).register(widget)
        }
    }

    override fun beforeRender(screen: Screen?, drawContext: GuiGraphics?, mouseX: Int, mouseY: Int, tickDelta: Float) {
        TextureChanger.renderingScreen = true
    }

    override fun afterRender(screen: Screen?, drawContext: GuiGraphics?, mouseX: Int, mouseY: Int, tickDelta: Float) {
        TextureChanger.renderingScreen = false
    }
}
