package opekope2.optigui.resource

import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import opekope2.util.delimiters
import opekope2.optigui.service.ResourceAccessService
import opekope2.optigui.service.getService
import opekope2.util.NumberOrRange
import opekope2.util.splitIgnoreEmpty
import opekope2.util.toBoolean
import org.ini4j.Ini
import org.ini4j.Options
import org.ini4j.Profile.Section
import java.io.File
import java.util.*

/**
 * Represents an OptiFine .properties file converted to the OptiGUI-compatible INI format.
 */
class OptiFineConvertedResource(private val wrappedResource: ResourceReader) : OptiGuiResource(wrappedResource) {
    override val ini: Ini by lazy {
        convert(Options().apply { load(wrappedResource.inputStream) }, wrappedResource.id) ?: Ini()
    }
}

private fun convert(properties: Options, path: Identifier): Ini? {
    if (properties["optigui.ignore"].toBoolean() == true) return null

    return Ini().also { ini ->
        ini.comment = path.toString()
        (converters[properties["container"] ?: return null] ?: return null)(properties, ini, path)
    }
}

private val generalPropertiesNoName = arrayOf(
    "biomes" to "biomes",
    "heights" to "heights"
)
private val generalProperties = arrayOf(
    *generalPropertiesNoName,
    "name" to "name"
)

private val dispenserVariants = setOf("dispenser", "dropper")
private val horseVariants = setOf("horse", "donkey", "mule", "llama")

private val converters = mapOf<String, (Options, Ini, Identifier) -> Unit>(
    "anvil" to createSimpleConverter("anvil chipped_anvil damaged_anvil", *generalPropertiesNoName),
    "beacon" to createSimpleConverter("beacon", *generalProperties, "levels" to "beacon.levels"),
    "brewing_stand" to createSimpleConverter("brewing_stand"),
    "chest" to { props, ini, path ->
        val chestVariants =
            if (props["trapped"].toBoolean() == true) "chest trapped_chest"
            else "chest"
        ini.add(chestVariants).also { section ->
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties, "large" to "chest.large", "christmas" to "chest.christmas")
        }
        if (props["ender"].toBoolean() == true) {
            ini.add("ender_chest").also { section ->
                props.resolveAndCopyReplacementTextureTo(section, path)
                props.copyTo(section, *generalProperties)
            }
        }
    },
    "crafting" to createSimpleConverter("crafting_table", *generalPropertiesNoName),
    "dispenser" to { props, ini, path ->
        val blocks = props["variants"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.toSet()
            ?.filter { it in dispenserVariants }
            ?: listOf("dispenser")
        blocks.map(ini::add).forEach { section ->
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties)
        }
    },
    "enchantment" to createSimpleConverter("enchanting_table"),
    "furnace" to createSimpleConverter("furnace"),
    "hopper" to createSimpleConverter("hopper"),
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
        }
        entities.map(ini::add).forEach { section ->
            props.resolveAndCopyReplacementTextureTo(section, path)
            props.copyTo(section, *generalProperties)
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
        }
    },
    "creative" to { props, ini, path ->
        val toReplace = props
            .entries
            .mapNotNull { (key, value) ->
                if (key.startsWith("texture.")) key.substring("texture.".length) to value
                else null
            }
        toReplace.forEachIndexed { index, (original, replacement) ->
            ini.add("#optifine:creative/$index").also { section ->
                section["interaction.texture"] =
                    if (original.endsWith(".png")) original
                    else "$original.png" // Workaround
                section["replacement"] = resolveReplacementTexture(replacement, path)
                props.copyTo(section, *generalPropertiesNoName)
            }
        }
    },
    "inventory" to createSimpleConverter("#optifine:inventory", *generalPropertiesNoName)
)

private fun createSimpleConverter(container: String, vararg copyProps: Pair<String, String> = generalProperties) =
    { props: Options, ini: Ini, path: Identifier ->
        val section = ini.add(container)
        props.resolveAndCopyReplacementTextureTo(section, path)
        props.copyTo(section, *copyProps)
    }

private val resourceAccess: ResourceAccessService by lazy(::getService)

private fun resolveReplacementTexture(texture: String, resourcePath: Identifier): String? {
    val resourceFolder = File(resourcePath.path).parent.replace('\\', '/')
    var texturePath = resolvePath(resourceFolder, texture) ?: return null

    if (!resourceAccess.getResource(texturePath).exists()) {
        texturePath = Identifier(texturePath.namespace, "${texturePath.path}.png")

        if (!resourceAccess.getResource(texturePath).exists()) return null
    }

    return texturePath.toString()
}

private fun resolvePath(resourcePath: String, path: String): Identifier? {
    val pathStack: Deque<String> = ArrayDeque()
    var tokenizer = StringTokenizer(resourcePath, "/")
    while (tokenizer.hasMoreTokens()) {
        pathStack.push(tokenizer.nextToken())
    }

    // Because there was a resource pack with two dangling tab characters after the resource name
    tokenizer = StringTokenizer(path.trim(), ":/", true)
    var namespace = Identifier.DEFAULT_NAMESPACE
    var nToken = -1

    while (tokenizer.hasMoreTokens()) {
        val token = tokenizer.nextToken()
        nToken++

        if (nToken == 0 && "~" == token) {
            pathStack.clear()
            pathStack.push("optifine")
            continue
        } else if (":" == token) {
            if (nToken == 1) {
                namespace = pathStack.pop()
                pathStack.clear()
            }
        } else if (".." == token) {
            if (pathStack.isEmpty()) return null

            pathStack.pop()
        } else if ("/" != token && "." != token) {
            pathStack.push(token)
        }
    }

    val pathBuilder = StringBuilder()
    var first = true

    while (!pathStack.isEmpty()) {
        if (first) first = false
        else pathBuilder.append("/")
        pathBuilder.append(pathStack.removeLast())
    }

    return Identifier(namespace, pathBuilder.toString())
}

private fun Options.copyTo(target: Section, vararg keyMap: Pair<String, String>) {
    for ((sourceKey, targetKey) in keyMap) {
        target[targetKey] = this[sourceKey] ?: continue
    }
}

private fun Options.resolveAndCopyReplacementTextureTo(target: Section, resourcePath: Identifier) {
    this["texture"]?.let { resolveReplacementTexture(it, resourcePath) }?.let { target["replacement"] = it }
}
