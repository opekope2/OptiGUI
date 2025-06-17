package opekope2.optigui.internal.inspector

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Encoder
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.text.Text
import opekope2.optigui.config.IConfig
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.resource.format.json.ILoadTimeNbtSupplier
import opekope2.optigui.resource.format.json.JsonFilterResource

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
        put("#$index", transformNbtFilterKeys(element))
    }
}

internal fun generateJsonResource(generatedBy: String): JsonElement? {
    val interaction = InteractionManager.interaction ?: return null
    val json = JsonObject()

    json.addProperty("generated_by", generatedBy)
    json.addProperty("docs", "https://opekope2.dev/OptiGUI/JSON.html")
    json.addProperty(JsonFilterResource.INVENTORIES_KEY, interaction.data.id.toString())
    json.add(JsonFilterResource.TEXTURE_CHANGES_KEY, getLastRenderedTextures())
    json.add(JsonFilterResource.SPRITE_CHANGES_KEY, getLastRenderedSprites())
    if (IConfig.get().dumpNbt) {
        json.add(JsonFilterResource.LOAD_FILTER_KEY, getLoadTimeNbtFilter() ?: return null)
        json.add(JsonFilterResource.FILTER_KEY, getInteractionNbtFilter(interaction) ?: return null)
    } else {
        val disabledText = Text.translatable("optigui.inspector.nbt_dumping_disabled").string
        json.add(JsonFilterResource.LOAD_FILTER_KEY, JsonPrimitive(disabledText))
        json.add(JsonFilterResource.FILTER_KEY, JsonPrimitive(disabledText))
    }

    return json
}

private fun getLastRenderedTextures() = JsonObject().apply {
    for (texture in InteractionManager.renderedTextures) {
        addProperty(texture.toString(), "example:path/to/changed/texture.png")
    }
}

private fun getLastRenderedSprites() = JsonObject().apply {
    for (texture in InteractionManager.renderedSprites) {
        addProperty(texture.toString(), "example:path/to/changed/sprite")
    }
}

private fun getLoadTimeNbtFilter(): JsonElement? {
    val loadTimeNbt = NbtCompound()
    for ((key, supplier) in ILoadTimeNbtSupplier) {
        loadTimeNbt.put(key, supplier.get())
    }

    return NBT_FILTER_JSON_ENCODER.encodeStart(JsonOps.INSTANCE, loadTimeNbt).result().orElse(null)
}

private fun getInteractionNbtFilter(interaction: Interaction): JsonElement? {
    return NBT_FILTER_JSON_ENCODER.encodeStart(JsonOps.INSTANCE, interaction.createNbt()).result().orElse(null)
}
