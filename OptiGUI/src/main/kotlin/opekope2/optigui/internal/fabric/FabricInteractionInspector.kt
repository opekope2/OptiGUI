@file: JvmName("Inspector")

package opekope2.optigui.internal.fabric

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Encoder
import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.data.IInteractionData
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.resource.load.ILoadTimeNbtSupplier
import opekope2.optigui.screen.IRetexturableScreen
import opekope2.optigui.toast.InspectorToast
import org.lwjgl.glfw.GLFW

// TODO inspector button on screen instead of a key binding
object FabricInteractionInspector : ScreenEvents.BeforeInit, ScreenKeyboardEvents.AfterKeyRelease {
    private val GSON = GsonBuilder().setLenient().setPrettyPrinting().create()
    private val NBT_FILTER_JSON_ENCODER: Encoder<NbtCompound> =
        NbtCompound.CODEC.comap(FabricInteractionInspector::transformNbtFilterKeys)
    private val KEY_BINDING = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.optigui.inspect",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F12,
            "key.categories.optigui"
        )
    )

    init {
        ScreenEvents.BEFORE_INIT.register(this)
    }

    private fun transformNbtFilterKeys(nbt: NbtElement): NbtElement = when (nbt) {
        is NbtCompound -> transformNbtFilterKeys(nbt)
        is AbstractNbtList<*> -> transformNbtFilterKeys(nbt)
        else -> nbt
    }

    @Suppress("NOTHING_TO_INLINE") // Stack size
    private inline fun transformNbtFilterKeys(nbt: NbtCompound): NbtCompound {
        val compound = NbtCompound()

        for (key in nbt.keys) {
            compound.put("@$key", transformNbtFilterKeys(nbt[key]!!))
        }

        return compound
    }

    @Suppress("NOTHING_TO_INLINE") // Stack size
    private inline fun transformNbtFilterKeys(nbt: AbstractNbtList<*>): NbtCompound = NbtCompound().apply {
        for ((index, element) in nbt.withIndex()) {
            put("@$index", transformNbtFilterKeys(element))
        }
    }

    override fun beforeInit(client: MinecraftClient?, screen: Screen?, scaledWidth: Int, scaledHeight: Int) {
        ScreenKeyboardEvents.afterKeyRelease(screen).register(this)
    }

    override fun afterKeyRelease(screen: Screen?, key: Int, scancode: Int, modifiers: Int) {
        if (screen !is IRetexturableScreen) return
        if (!KEY_BINDING.matchesKey(key, scancode)) return

        val inspection = GSON.toJson(createJsonResource(InteractionManager.interaction?.data ?: return) ?: return)

        MinecraftClient.getInstance().keyboard.clipboard = inspection
        MinecraftClient.getInstance().toastManager.add(InspectorToast())
    }

    private fun createJsonResource(interactionData: IInteractionData): JsonElement? = JsonObject().apply {
        addProperty(JsonFilterResource.CONTAINERS_KEY, interactionData.id.toString())
        add(JsonFilterResource.TEXTURES_KEY, getLastRenderedTextures())
        add(JsonFilterResource.LOAD_FILTER_KEY, getLoadTimeNbtFilter())
        add(JsonFilterResource.FILTER_KEY, getInteractionNbtFilter() ?: return null)
    }

    private fun getLastRenderedTextures() = JsonObject().apply {
        for (texture in InteractionManager.renderedTextures) {
            addProperty(texture.toString(), "example:path/to/changed/texture.png")
        }
    }

    private fun getLoadTimeNbtFilter(): JsonElement? {
        val loadTimeNbt = NbtCompound()
        for ((key, supplier) in ILoadTimeNbtSupplier) {
            loadTimeNbt.put(key, supplier.get())
        }

        return NBT_FILTER_JSON_ENCODER.encodeStart(JsonOps.INSTANCE, loadTimeNbt).result().orElse(null)
    }

    private fun getInteractionNbtFilter(): JsonElement? {
        val interaction = InteractionManager.interaction ?: return null

        return NBT_FILTER_JSON_ENCODER.encodeStart(JsonOps.INSTANCE, interaction.createNbt()).result().orElse(null)
    }
}
