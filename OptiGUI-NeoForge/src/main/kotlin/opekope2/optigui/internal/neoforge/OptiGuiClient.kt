package opekope2.optigui.internal.neoforge

import com.google.common.base.Suppliers
import net.minecraft.client.gui.screens.Screen
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.ScreenEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.event.tick.LevelTickEvent
import opekope2.optigui.config.IConfig
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.internal.IOptiGuiPlatform
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.internal.neoforge.event_handler.NeoForgeAttackHandler
import opekope2.optigui.internal.neoforge.event_handler.NeoForgeInteractionHandler
import opekope2.optigui.internal.neoforge.filter.NbtVersionFilter
import opekope2.optigui.internal.neoforge.gui.widget.NeoForgeInspectorWidget
import opekope2.optigui.internal.neoforge.nbt_provider.NeoForgeModsNbtProvider
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen
import opekope2.optigui.util.MOD_ID
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import kotlin.jvm.optionals.getOrDefault

@Mod(MOD_ID, dist = [Dist.CLIENT])
internal class OptiGuiClient(modContainer: ModContainer) : IOptiGuiPlatform, IConfigScreenFactory {
    init {
        IOptiGuiPlatform.initialize(this)
        registerNbtFilters()
        registerLoadTimeNbtSuppliers()
        FORGE_BUS.register(NeoForgeInteractionHandler)
        FORGE_BUS.register(NeoForgeAttackHandler)
        FORGE_BUS.register(this)
        modContainer.registerExtensionPoint(IConfigScreenFactory::class.java, this)
    }

    val issueTrackerUrlGetter = Suppliers.memoize {
        modContainer.modInfo.owningFile.config.getConfigElement<String>("issueTrackerURL")
            .getOrDefault("(failed to read issue tracker URL)")
    }

    override val version = modContainer.modInfo.version.toString()

    override fun isModInstalled(modId: String) = ModList.get().isLoaded(modId)

    private fun registerNbtFilters() {
        INbtFilter.Registry.register(">v", NbtVersionFilter.Type.VERSION_GREATER)
        INbtFilter.Registry.register(">=v", NbtVersionFilter.Type.VERSION_GREATER_EQUAL)
        INbtFilter.Registry.register("=v", NbtVersionFilter.Type.VERSION_EQUAL)
        INbtFilter.Registry.register("!=v", NbtVersionFilter.Type.VERSION_NOT_EQUAL)
        INbtFilter.Registry.register("<=v", NbtVersionFilter.Type.VERSION_LESS_EQUAL)
        INbtFilter.Registry.register("<v", NbtVersionFilter.Type.VERSION_LESS)
        INbtFilter.Registry.register("~v", NbtVersionFilter.Type.VERSION_SAME_TO_NEXT_MINOR)
        INbtFilter.Registry.register("^v", NbtVersionFilter.Type.VERSION_SAME_TO_NEXT_MAJOR)
    }

    private fun registerLoadTimeNbtSuppliers() {
        ILoadTimeNbtProvider.register("mods", NeoForgeModsNbtProvider(this))
    }

    @SubscribeEvent
    fun afterWorldTick(event: LevelTickEvent.Post) {
        if (!InteractionManager.isInteracting) return
        InteractionManager.clearCache()
    }

    @SubscribeEvent
    fun onDisconnect(event: ClientPlayerNetworkEvent.LoggingOut) {
        InteractionManager.end(disconnected = true)
    }

    @SubscribeEvent
    fun afterScreenInit(event: ScreenEvent.Init.Post) {
        if (event.screen is ITextureChangeableScreen && IConfig.get().enableInspector) {
            event.addListener(NeoForgeInspectorWidget())
        }
    }

    @SubscribeEvent
    fun beforeScreenRender(event: ScreenEvent.Render.Pre) {
        TextureChanger.renderingScreen = true
    }

    @SubscribeEvent
    fun afterScreenRender(event: ScreenEvent.Render.Post) {
        TextureChanger.renderingScreen = false
    }

    override fun createScreen(container: ModContainer, modListScreen: Screen) =
        IConfig.createConfigScreen(modListScreen)
}

