package opekope2.optigui.resource

import net.minecraft.util.DyeColor
import opekope2.optigui.internal.optifinecompat.delimiters
import opekope2.util.NumberOrRange
import opekope2.util.splitIgnoreEmpty
import opekope2.util.toBoolean
import org.ini4j.Ini
import org.ini4j.Options
import org.ini4j.Profile

/**
 * Represents an OptiFine .properties file converted to the OptiGUI-compatible INI format.
 */
class OptiFineConvertedResource(private val wrappedResource: ResourceReader) :
    OptiGuiResource(wrappedResource) {
    override val ini: Ini by lazy { convert(Options().apply { load(wrappedResource.inputStream) }) ?: Ini() }
}

private fun convert(properties: Options): Ini? {
    if (properties["optigui.ignore"].toBoolean() == true) return null

    return Ini().also { ini ->
        (converters[properties["container"] ?: return null] ?: return null)(properties, ini)
    }
}

private val generalPropertiesNoName = arrayOf(
    "texture" to "texture",
    "biomes" to "biomes",
    "heights" to "heights"
)
private val generalProperties = arrayOf(
    *generalPropertiesNoName,
    "name" to "name"
)

private val dispenserVariants = setOf("dispenser", "dropper")
private val horseVariants = setOf("horse", "donkey", "mule", "llama")

private val converters = mapOf<String, (Options, Ini) -> Unit>(
    "anvil" to createSimpleConverter("anvil chipped_anvil damaged_anvil", *generalPropertiesNoName),
    "beacon" to createSimpleConverter("beacon", *generalProperties, "levels" to "beacon.levels"),
    "brewing_stand" to createSimpleConverter("brewing_stand"),
    "chest" to { props, ini ->
        val chestVariants =
            if (props["trapped"].toBoolean() == true) "chest trapped_chest"
            else "chest"
        ini.add(chestVariants).also { section ->
            props.copyTo(section, *generalProperties, "large" to "chest.large", "christmas" to "chest.christmas")
        }
        if (props["ender"].toBoolean() == true) {
            ini.add("ender_chest").also { section ->
                props.copyTo(section, *generalProperties)
            }
        }
    },
    "crafting" to createSimpleConverter("crafting_table", *generalPropertiesNoName),
    "dispenser" to { props, ini ->
        val blocks = props["variants"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.toSet()
            ?.filter { it in dispenserVariants }
            ?: listOf("dispenser")
        blocks.map(ini::add).forEach {
            props.copyTo(it, *generalProperties)
        }
    },
    "enchantment" to createSimpleConverter("enchanting_table"),
    "furnace" to createSimpleConverter("furnace"),
    "hopper" to createSimpleConverter("hopper"),
    "horse" to { props, ini ->
        val entities = props["variants"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.filter { it in horseVariants }
            ?.toMutableSet()
            ?: horseVariants.toMutableSet()
        if (entities.remove("llama")) {
            props.copyTo(ini.add("llama"), *generalProperties, "colors" to "llama.colors")
        }
        entities.map(ini::add).forEach { section ->
            props.copyTo(section, *generalProperties)
        }
    },
    "villager" to { props, ini ->
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
            props.copyTo(section, *generalProperties)
            if (professions.isNotEmpty()) {
                section["villager.professions"] = professions.joinToString(" ")
            }
        }
    },
    "shulker_box" to { props, ini ->
        val blocks = props["colors"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.toSet()
            ?.filter { DyeColor.byName(it, null) != null }
            ?.map { "${it}_shulker_box" }
            ?: listOf("shulker_box")
        blocks.map(ini::add).forEach { section ->
            props.copyTo(section, *generalProperties)
        }
    },
    /*"creative" to { props, ini ->
        props.copyTo(ini.add(""), *generalProperties)
        TODO()
    },*/
    "inventory" to createSimpleConverter("optigui:inventory", *generalPropertiesNoName) // TODO
)

private fun createSimpleConverter(container: String, vararg copyProps: Pair<String, String> = generalProperties) =
    { props: Options, ini: Ini ->
        props.copyTo(ini.add(container), *copyProps)
    }

private fun Options.copyTo(section: Profile.Section, vararg keyMap: Pair<String, String>) {
    for ((sourceKey, targetKey) in keyMap) {
        section[targetKey] = this[sourceKey] ?: continue
    }
}
