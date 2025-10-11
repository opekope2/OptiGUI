package opekope2.optigui.internal.inspector

import com.google.gson.JsonObject
import opekope2.optigui.config.IConfig
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.resource.format.json.JsonFilterResource

internal object Inspector {
    fun generateJsonResource(interaction: IInteraction, generatedBy: String) = JsonObject().also { json ->
        json.addProperty("\$schema", "https://opekope2.dev/OptiGUI/json_v2.schema.json")
        json.addProperty("generated_by", generatedBy)
        json.addProperty("docs", "https://opekope2.dev/OptiGUI/JSON.html")
        json.addProperty(JsonFilterResource.FORMAT_KEY, JsonFilterResource.NEWEST_FORMAT)
        json.addTarget(interaction.target)
        json.add(JsonFilterResource.V2.TEXTURE_CHANGERS_KEY, getLastRenderedTextures())
        json.add(JsonFilterResource.V2.SPRITE_CHANGERS_KEY, getLastRenderedSprites())
        val nbtDumper = IConfig.get().dumpNbt
        json.add(JsonFilterResource.V2.TEXT_STYLE_CHANGERS_KEY, nbtDumper.getLastRenderedTexts())
        json.add(JsonFilterResource.V2.LOAD_FILTER_KEY, nbtDumper.getLoadTimeNbt())
        json.add(JsonFilterResource.V2.FILTER_KEY, nbtDumper.getInteractionNbt(interaction))
    }

    private fun JsonObject.addTarget(target: InteractionTarget) {
        when (target) {
            is InteractionTarget.Block -> addProperty(JsonFilterResource.V2.BLOCKS_KEY, target.id.toString())
            is InteractionTarget.Entity -> addProperty(JsonFilterResource.V2.ENTITIES_KEY, target.id.toString())
            is InteractionTarget.Item -> addProperty(JsonFilterResource.V2.ITEMS_KEY, target.id.toString())
            InteractionTarget.Inventory -> addProperty(JsonFilterResource.V2.INVENTORY_KEY, true)
            InteractionTarget.Unknown -> addProperty(JsonFilterResource.V2.UNKNOWN_KEY, true)
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
