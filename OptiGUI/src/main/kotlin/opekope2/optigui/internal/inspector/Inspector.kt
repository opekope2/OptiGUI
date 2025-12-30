package opekope2.optigui.internal.inspector

import com.google.gson.JsonObject
import opekope2.optigui.config.IConfig
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.JSON_RESOURCE_DOCS_URL
import opekope2.optigui.util.JSON_SCHEMA_V2_URL

internal object Inspector {
    fun generateJsonResource(interaction: IInteraction, generatedBy: String) = JsonObject().also { json ->
        json.addProperty("\$schema", JSON_SCHEMA_V2_URL)
        json.addProperty("generated_by", generatedBy)
        json.addProperty("docs", JSON_RESOURCE_DOCS_URL)
        json.addTarget(interaction.target)
        json.add(JsonFilterResource.TEXTURE_CHANGERS_KEY, getLastRenderedTextures())
        json.add(JsonFilterResource.SPRITE_CHANGERS_KEY, getLastRenderedSprites())
        val nbtDumper = IConfig.get().dumpNbt
        json.add(JsonFilterResource.TEXT_STYLE_CHANGERS_KEY, nbtDumper.getLastRenderedTexts())
        json.add(JsonFilterResource.LOAD_FILTER_KEY, nbtDumper.getLoadTimeNbt())
        json.add(JsonFilterResource.FILTER_KEY, nbtDumper.getInteractionNbt(interaction))
    }

    private fun JsonObject.addTarget(target: InteractionTarget) {
        when (target) {
            is InteractionTarget.Block -> addProperty(JsonFilterResource.BLOCKS_KEY, target.id.toString())
            is InteractionTarget.Entity -> addProperty(JsonFilterResource.ENTITIES_KEY, target.id.toString())
            is InteractionTarget.Item -> addProperty(JsonFilterResource.ITEMS_KEY, target.id.toString())
            InteractionTarget.Inventory -> addProperty(JsonFilterResource.INVENTORY_KEY, true)
            InteractionTarget.Unknown -> addProperty(JsonFilterResource.UNKNOWN_KEY, true)
        }
    }

    private fun getLastRenderedTextures() = JsonObject().also { json ->
        for (texture in InteractionManager.renderedTextures) {
            json.addProperty(texture.toString(), "example:path/to/changed/texture.png")
        }
    }

    private fun getLastRenderedSprites() = JsonObject().also { json ->
        for (texture in InteractionManager.renderedSprites) {
            json.addProperty(texture.toString(), "example:path/to/changed/sprite")
        }
    }
}
