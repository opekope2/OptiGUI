@file: JvmName("Inspector")

package opekope2.optigui.internal.interaction

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Encoder
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.ILoadTimeNbtSupplier
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.data.IInteractionData
import opekope2.optigui.resource.json.JsonFilterResource

private val GSON = GsonBuilder().setLenient().setPrettyPrinting().create()
private val NBT_FILTER_JSON_ENCODER: Encoder<NbtCompound> = NbtCompound.CODEC.comap(::transformNbtFilterKeys)

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

internal fun inspectInteraction(): String? {
    return GSON.toJson(createJsonResource(InteractionManager.interactionData ?: return null))
}

internal fun createJsonResource(interactionData: IInteractionData): JsonElement? = JsonObject().apply {
    addProperty("gui", interactionData.id.toString())
    add("textures", getLastRenderedTextures())
    add("match", getInteractionNbtFilter() ?: return null)
}

private fun getLastRenderedTextures() = JsonObject().apply {
    for (texture in InteractionManager.lastFrameRenderedTextures) {
        addProperty(texture.toString(), "example:path/to/replacement.png")
    }
}

private fun getInteractionNbtFilter(): JsonElement? {
    val texture = InteractionManager.lastFrameRenderedTextures.firstOrNull() ?: return null
    val interaction = InteractionManager.createInteraction(texture) ?: return null

    return NBT_FILTER_JSON_ENCODER.encodeStart(JsonOps.INSTANCE, interaction.createNbt()).result().orElse(null)
}
