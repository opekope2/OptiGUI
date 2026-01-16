package opekope2.optigui.internal.inspector

import dev.runefox.json.JsonNode
import dev.runefox.json.JsonObject
import opekope2.optigui.config.config
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.JSON_RESOURCE_SCHEMA_URL

internal object Inspector {
    fun generateJsonResource(interaction: IInteraction) = JsonObject {
        it["\$schema"] = JSON_RESOURCE_SCHEMA_URL
        it.addTarget(interaction.target)
        it[JsonFilterResource.TEXTURE_CHANGERS_KEY] = getLastRenderedTextures()
        it[JsonFilterResource.SPRITE_CHANGERS_KEY] = getLastRenderedSprites()
        val nbtDumper = config.dumpNbt()
        it[JsonFilterResource.TEXT_STYLE_CHANGERS_KEY] = nbtDumper.getLastRenderedTexts()
        it[JsonFilterResource.LOAD_FILTER_KEY] = nbtDumper.getLoadTimeNbt()
        it[JsonFilterResource.FILTER_KEY] = nbtDumper.getInteractionNbt(interaction)
    }

    private fun JsonNode.addTarget(target: InteractionTarget) {
        when (target) {
            is InteractionTarget.Block -> set(JsonFilterResource.BLOCKS_KEY, target.id.toString())
            is InteractionTarget.Entity -> set(JsonFilterResource.ENTITIES_KEY, target.id.toString())
            is InteractionTarget.Item -> set(JsonFilterResource.ITEMS_KEY, target.id.toString())
            InteractionTarget.Inventory -> set(JsonFilterResource.INVENTORY_KEY, true)
            InteractionTarget.Unknown -> set(JsonFilterResource.UNKNOWN_KEY, true)
        }
    }

    private fun getLastRenderedTextures() = JsonObject().also {
        for (texture in InteractionManager.renderedTextures) {
            it[texture.toString()] = "example:path/to/changed/texture.png"
        }
    }

    private fun getLastRenderedSprites() = JsonObject().also {
        for (texture in InteractionManager.renderedSprites) {
            it[texture.toString()] = "example:path/to/changed/sprite"
        }
    }
}
