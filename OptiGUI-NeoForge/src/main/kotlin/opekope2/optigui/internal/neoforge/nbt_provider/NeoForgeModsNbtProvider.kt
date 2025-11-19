package opekope2.optigui.internal.neoforge.nbt_provider

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.neoforged.fml.ModList
import net.neoforged.neoforgespi.language.IModInfo
import opekope2.optigui.internal.neoforge.OptiGuiClient
import opekope2.optigui.internal.neoforge.dfu.NightConfigOps
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider
import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull

internal class NeoForgeModsNbtProvider(private val mod: OptiGuiClient) : ILoadTimeNbtProvider {
    private val logger = LoggerFactory.getLogger(NeoForgeModsNbtProvider::class.java)
    private val suppressedModErrors = mutableSetOf<String>()

    override fun get() = CompoundTag().apply {
        for (mod in ModList.get().mods) {
            put(mod.modId, CompoundTag().apply {
                putString("version", mod.version.toString())
            })
        }
    }

    private fun getModProperties(mod: IModInfo) = try {
        NightConfigOps.convertTo(NbtOps.INSTANCE, mod.modProperties)
    } catch (e: Exception) {
        if (mod.modId !in suppressedModErrors) {
            logError(mod, e)
            suppressedModErrors += mod.modId // Do not spam console each time evaluated
        }
        null
    }

    private fun logError(mod: IModInfo, error: Exception) {
        logger.atError()
            .setCause(error)
            .addArgument(mod.displayName)
            .addArgument(mod.modId)
            .addArgument(mod.version)
            .addArgument(mod.modURL::getOrNull)
            .addArgument(this.mod.issueTrackerUrlGetter)
            .log("Error encoding modproperties of mod {} (modId={},version={},modURL={}). THIS IS A BUG. Please report this at OptiGUI issue tracker: {}")
    }
}
