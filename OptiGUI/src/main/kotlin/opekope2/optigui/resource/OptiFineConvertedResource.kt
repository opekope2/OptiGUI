package opekope2.optigui.resource

import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import opekope2.optigui.service.ResourceAccessService
import opekope2.optigui.service.getService
import opekope2.util.*
import org.ini4j.Ini
import org.ini4j.Options
import org.ini4j.Profile.Section

/**
 * Represents an OptiFine .properties file converted to the OptiGUI-compatible INI format.
 */
class OptiFineConvertedResource(private val wrappedResource: ResourceReader) : OptiGuiResource(wrappedResource) {
    override val ini = convert(Options().apply { load(wrappedResource.inputStream) }, wrappedResource.id)
}

private fun convert(properties: Options, path: Identifier) = Ini().also { ini ->
    ini.comment = buildString {
        appendLine("This resource has been converted from the OptiFine format.")
        appendLine("Resource path: $path")
        appendLine("Original OptiFine resource (parsed):")
        appendLine()

        properties.store(this)
    }

    if (properties["texture"] != null) {
        // If texture is not specified, texture.<path> may be.
        // If it isn't either, OptiGUI will show a "resource pack empty" warning.
        (converters[properties["container"] ?: return@also] ?: return@also)(properties, ini, path)
    }

    texturePathConverter(properties, ini, path)

    if (properties["optigui.ignore"].toBoolean() == true) {
        ini.entries.forEach { (_, section) ->
            section["if"] = "false"
        }
    }
}

private val generalProperties = arrayOf(
    "biomes" to "biomes",
    "heights" to "heights"
)

private val dispenserVariants = setOf("dispenser", "dropper")
private val horseVariants = setOf("horse", "donkey", "mule", "llama")

private typealias Converter = (Options, Ini, Identifier) -> Unit

private val texturePathConverter: Converter = { props, ini, path ->
    val toReplace = props
        .entries
        .mapNotNull { (key, value) ->
            if (key.startsWith("texture.")) key.substring("texture.".length) to value
            else null
        }
    toReplace.forEachIndexed { index, (original, replacement) ->
        ini.add("#optifine:texture_path_$index").also { section ->
            section["interaction.texture"] = "minecraft:textures/gui/" +
                    if (original.endsWith(".png")) original
                    else "$original.png" // Workaround
            section["replacement"] = resolveReplacementTexture(replacement, path)
            props.copyTo(section, *generalProperties)
        }
    }
}

private val converters = mapOf<String, Converter>(
    "anvil" to SimpleConverter("anvil chipped_anvil damaged_anvil", copyName = false, *generalProperties),
    "beacon" to SimpleConverter("beacon", copyName = true, *generalProperties, "levels" to "beacon.levels"),
    "brewing_stand" to SimpleConverter("brewing_stand", copyName = true, *generalProperties),
    "chest" to { props, ini, path ->
        // Lots of pain and suffering from this retarded syntax, thanks OptiFine
        val large = props["large"].toBoolean()
        val trapped = props["trapped"].toBoolean()
        val ender = props["ender"].toBoolean()
        val christmas = props["christmas"].toBoolean()

        if (ender != true) {
            val chestVariants = when (trapped) {
                true -> "trapped_chest"
                false -> "chest"
                else -> "chest trapped_chest"
            }
            ini.add(chestVariants).also { section ->
                props.resolveAndCopyReplacementTextureTo(section, path)
                props.copyTo(section, *generalProperties, "large" to "chest.large")
                props.copyNameTo(section)
                when (christmas) {
                    true -> section["date"] = "dec@24-26"
                    false -> section["date"] = "1 2 3 4 5 6 7 8 9 spooktober 11 dec@1-23 dec@27-31"
                    null -> {}
                }
            }
        }
        // Ender chests can't be large, trapped, and can't be named
        if (ender != false && large != true && trapped != true && props["name"].isNullOrEmpty()) {
            ini.add("ender_chest").also { section ->
                props.resolveAndCopyReplacementTextureTo(section, path)
                props.copyTo(section, *generalProperties)
            }
        }
    },
    "crafting" to SimpleConverter("crafting_table", copyName = false, *generalProperties),
    "dispenser" to { props, ini, path ->
        val blocks = props["variants"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.toSet()
            ?.filter { it in dispenserVariants }
            ?: listOf("dispenser")
        blocks.map(ini::add).forEach { section ->
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties)
            props.copyNameTo(section)
        }
    },
    "enchantment" to SimpleConverter("enchanting_table", copyName = true, *generalProperties),
    "furnace" to SimpleConverter("furnace", copyName = true, *generalProperties),
    "hopper" to SimpleConverter("hopper", copyName = true, *generalProperties),
    "horse" to { props, ini, path ->
        val entities = props["variants"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.filter { it in horseVariants }
            ?.toMutableSet()
            ?: horseVariants.toMutableSet()
        if (entities.remove("llama")) {
            val section = ini.add("llama")
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties, "colors" to "llama.colors")
            props.copyNameTo(section)
        }
        entities.map(ini::add).forEach { section ->
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties)
            props.copyNameTo(section)
        }
    },
    "villager" to { props, ini, path ->
        val professions = sequence {
            props["professions"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.map { it.split(":") }
                ?.forEach { prof ->
                    when (prof.size) {
                        1 -> yield(prof[0])
                        2 -> prof[1]
                            .splitIgnoreEmpty(',')
                            .mapNotNull(NumberOrRange::tryParse)
                            .forEach {
                                yield("${prof[0]}@$it")
                            }
                    }
                }
        }.toSet()
        ini.add("villager").also { section ->
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties)
            props.copyNameTo(section)
            if (professions.isNotEmpty()) {
                section["villager.professions"] = professions.joinToString(" ")
            }
        }
    },
    "shulker_box" to { props, ini, path ->
        val blocks = props["colors"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.toSet()
            ?.filter { DyeColor.byName(it, null) != null }
            ?.map { "${it}_shulker_box" }
            ?: listOf("shulker_box")
        blocks.map(ini::add).forEach { section ->
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties)
            props.copyNameTo(section)
        }
    },
    "creative" to { _, _, _ -> },
    "inventory" to { props, ini, path ->
        ini.add("#optifine:inventory").also { section ->
            section["interaction.texture"] = TexturePath.INVENTORY.toString()
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties)
        }
    }
)

private class SimpleConverter(
    private val container: String,
    private val copyName: Boolean,
    private vararg val copyProps: Pair<String, String>
) : Converter {
    override fun invoke(props: Options, ini: Ini, path: Identifier) {
        val section = ini.add(container)
        props.resolveAndCopyReplacementTextureTo(section, path)
        props.copyTo(section, *copyProps)
        if (copyName) props.copyNameTo(section)
    }
}

private val resourceAccess: ResourceAccessService by lazy(::getService)

private fun resolveReplacementTexture(texture: String, resourcePath: Identifier): String? {
    var texturePath = resolvePath(texture, resourcePath, OPTIFINE_TILDE_PATH) ?: return null

    if (resourceAccess.getResource(texturePath).exists()) return texturePath.toString()

    texturePath = texturePath.run { Identifier(namespace, "$path.png") }

    return if (resourceAccess.getResource(texturePath).exists()) texturePath.toString()
    else null
}

private fun Options.copyTo(target: Section, vararg keyMap: Pair<String, String>) {
    for ((sourceKey, targetKey) in keyMap) {
        target[targetKey] = this[sourceKey] ?: continue
    }
}

private fun Options.copyNameTo(target: Section) {
    val name = this["name"] ?: return
    when {
        name.startsWith("pattern:") -> target["name.wildcard"] = name.substring("pattern:".length)
        name.startsWith("ipattern:") -> target["name.wildcard.ignore_case"] = name.substring("ipattern:".length)
        name.startsWith("regex:") -> target["name.regex"] = name.substring("regex:".length)
        name.startsWith("iregex:") -> target["name.regex.ignore_case"] = name.substring("iregex:".length)
        else -> target["name"] = name
    }
}

private fun Options.resolveAndCopyReplacementTextureTo(target: Section, resourcePath: Identifier) {
    target["replacement"] = this["texture"]?.let { resolveReplacementTexture(it, resourcePath) ?: it }
}
