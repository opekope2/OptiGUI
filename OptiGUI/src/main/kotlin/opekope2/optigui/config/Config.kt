package opekope2.optigui.config

import com.mojang.serialization.Codec
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.dsl.YetAnotherConfigLib
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.controller
import dev.isxander.yacl3.dsl.descriptionBuilder
import dev.isxander.yacl3.platform.YACLPlatform
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.StringIdentifiable
import opekope2.optigui.util.MOD_ID

object Config : JsonFileCodecConfig<Config>(YACLPlatform.getConfigDir().resolve("optigui.json")) {
    val enableInspector by register(true, BOOL)

    init {
        if (!loadFromFile()) saveToFile()
    }

    @Suppress("UnusedVariable")
    fun createConfigScreen(parent: Screen): Screen = YetAnotherConfigLib(MOD_ID) {
        save { saveToFile() }

        val optiGui by categories.registering {
            val inspectorOptions by groups.registering {
                val enableInspectorOption by options.registering<Boolean> {
                    binding = enableInspector.asBinding()
                    controller = TickBoxControllerBuilder::create
                    descriptionBuilder {
                        addDefaultText(lines = 1)
                    }
                }
            }
        }
    }.generateScreen(parent)

    enum class ResourceLoadingErrorFilter() : StringIdentifiable, NameableEnum {
        NOTHING,
        ERRORS_ONLY,
        ERRORS_AND_WARNINGS;

        override fun asString() = name

        override fun getDisplayName(): Text = Text.translatable("optigui.enum.ResourceLoadingErrorFilter.$name")

        companion object {
            val CODEC: Codec<ResourceLoadingErrorFilter> =
                StringIdentifiable.createCodec(ResourceLoadingErrorFilter::values)
        }
    }
}
