package opekope2.optigui.resource

import com.google.gson.JsonSyntaxException
import net.minecraft.resource.ResourceManager
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import opekope2.optigui.internal.util.assertNotEmpty
import opekope2.optigui.internal.util.delimiters
import opekope2.optigui.internal.util.eventBuilder
import opekope2.optigui.internal.util.splitIgnoreEmpty
import opekope2.optigui.registry.ContainerDefaultGuiTextureRegistry
import opekope2.optigui.util.*
import org.ini4j.Ini
import org.ini4j.Profile
import org.slf4j.Logger
import org.slf4j.event.Level.ERROR
import org.slf4j.event.Level.WARN
import java.util.regex.Pattern

/**
 * OptiGUI INI filter loader.
 */
class OptiGuiFilterLoader : IFilterLoader {
    override fun loadRawFilters(resourceManager: ResourceManager, logger: Logger): Collection<IRawFilterData> {
        return resourceManager.findResources(OPTIGUI_RESOURCES_ROOT) { (ns, path) ->
            ns == MOD_ID && path.endsWith(".ini")
        }.map { (id, resource) ->
            id to resource.inputStream.use(::Ini)
        }.flatMap { (id, ini) ->
            ini.flatMap loadSection@{ (sectionName, section) ->
                val containers = sectionName
                    ?.splitIgnoreEmpty(*delimiters)
                    ?.assertNotEmpty()
                    ?.filter { !it.startsWith('#') }
                    ?.mapNotNull { container ->
                        Identifier.tryParse(container) ?: run {
                            logger.eventBuilder(ERROR, id, container).log(
                                "Invalid container identifier `{}` in `{}`",
                                container, id
                            )
                            null
                        }
                    }
                    ?.ifEmpty { null }
                    ?: return@loadSection setOf()

                val replacement = section["replacement"]?.let { resolvePath(it, id) }
                if (replacement == null) {
                    logger.eventBuilder(WARN, id, sectionName).log(
                        "Ignoring section [{}] in `{}`, because replacement texture `{}` cannot be resolved",
                        sectionName, id, section["replacement"]
                    )
                    return@loadSection setOf()
                }

                section -= "replacement"

                var priority = 0
                if ("load.priority" in section) {
                    val loadPriority = section["load.priority"]!!
                    priority = loadPriority.toIntOrNull() ?: run {
                        logger.eventBuilder(WARN, id, sectionName).log(
                            "Ignoring section [{}] in `{}`, because load priority `{}` is malformed",
                            sectionName, id, loadPriority
                        )
                        return@loadSection setOf()
                    }
                    section -= "load.priority"
                }

                var title = Text.empty()//TODO title
                if ("title.json" in section) {
                    val titleJson = section["title.json"]!!
                    try {
                        title = Text.Serializer.fromJson(titleJson)
                    } catch (e: JsonSyntaxException) {
                        logger.eventBuilder(WARN, id, sectionName).log(
                            "Ignoring section [{}] in `{}`, because title json `{}` is malformed",
                            sectionName, id, titleJson
                        )
                        return@loadSection setOf()
                    }

                    if(titleJson.contains("\"text\":\"this_name\"")) { //if player want to use current container name
                        title = Text.empty().setStyle(title.style)
                    }

                    section -= "title.json"
                    section -= "title.name"
                    section -= "title.style"
                } else {
                    if ("title.name" in section) {
                        val titleName = section["title.name"]!!
                        title = Text.translatable(titleName)
                        section -= "title.name"
                    }

                    if ("title.style" in section) {
                        val titleStyle = section["title.style"]!!
                        val styles = titleStyle.split(Pattern.compile(" "))
                        var style = Style.EMPTY
                        for (st in styles) {
                            val format = Formatting.byName(st.lowercase())
                            style = if (format != null) style.withFormatting(format)
                            else {
                                val color = TextColor.parse(st)
                                if (color != null) {
                                    style.withColor(color)
                                } else {
                                    logger.eventBuilder(WARN, id, sectionName).log(
                                        "Ignoring section [{}] in `{}`, because title style `{}` is malformed in `{}`",
                                        sectionName, id, titleStyle, st
                                    )
                                    return@loadSection setOf()
                                }
                            }
                        }
                        title = title.setStyle(style)
                        section -= "title.style"
                    }
                }
                
                containers.map { FilterData(priority, id, it, replacement, title, section) }
            }
        }
    }

    private class FilterData(
        override val priority: Int,
        override val resource: Identifier,
        override val container: Identifier?,
        override val replacementTexture: Identifier,
        override val title: Text,//TODO title
        private val section: Profile.Section
    ) : IRawFilterData {
        override var replaceableTextures: Set<Identifier> = (
                if ("interaction.texture" in section) Identifier.tryParse(section["interaction.texture"]!!)
                else container?.let(ContainerDefaultGuiTextureRegistry::get)
                )?.let(::setOf) ?: setOf()

        override val rawSelectorData: Iterable<Pair<String, String>>
            get() = Iterable {
                object : Iterator<Pair<String, String>> {
                    private val iterator = section.iterator()

                    override fun hasNext(): Boolean = iterator.hasNext()

                    override fun next(): Pair<String, String> {
                        val (key, value) = iterator.next()
                        return key to value
                    }
                }
            }
    }
}
