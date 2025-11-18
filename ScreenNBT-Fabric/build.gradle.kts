import net.fabricmc.loom.api.processor.MinecraftJarProcessor
import net.fabricmc.loom.api.processor.ProcessorContext
import net.fabricmc.loom.api.processor.SpecContext
import net.fabricmc.tinyremapper.OutputConsumerPath
import net.fabricmc.tinyremapper.TinyRemapper
import opekope2.optigui.buildscript.extension.Version
import opekope2.optigui.buildscript.extension.commonProject

plugins {
    id("opekope2.optigui.buildscript.plugin.Loader")
    alias(libs.plugins.fabric.loom)
}

version = Version.fabric(libs.versions.optigui, libs.versions.minecraft)

base {
    archivesName = "screen-nbt-fabric"
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered { officialMojangMappings(); parchment(libs.parchment) })
    modImplementation(libs.fabric.loader)

    commonProject(":ScreenNBT")
}

loom {
    runtimeOnlyLog4j = true

    // Unfuck MethodsReturnNonnullByDefault
    val jetbrainsToJsr305 = mapOf(
        "org/jetbrains/annotations/Nullable" to "javax/annotation/Nullable",
        "org/jetbrains/annotations/NotNull" to "javax/annotation/Nonnull",
        "org/jetbrains/annotations/Unmodifiable" to "javax/annotation/concurrent/Immutable"
    )
    addMinecraftJarProcessor(RemappingJarProcessor::class.java, "optigui:jsr305-annotations", jetbrainsToJsr305)
}


abstract class RemappingJarProcessor @Inject constructor(
    private val name: String,
    private val mappings: Map<String, String>
) : MinecraftJarProcessor<RemappingJarProcessor.Spec> {
    override fun buildSpec(context: SpecContext?) = Spec(mappings)

    override fun processJar(jar: java.nio.file.Path, spec: Spec, context: ProcessorContext?) {
        val tinyRemapper: TinyRemapper = TinyRemapper.newRemapper()
            .withMappings { out -> spec.mappings.forEach(out::acceptClass) }
            .build()

        try {
            OutputConsumerPath.Builder(jar).build().use { outputConsumer ->
                tinyRemapper.readInputs(jar)
                tinyRemapper.apply(outputConsumer)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to remap JAR $jar", e)
        } finally {
            tinyRemapper.finish()
        }
    }

    override fun getName() = name

    data class Spec(val mappings: Map<String, String>) : MinecraftJarProcessor.Spec
}
