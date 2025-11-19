import net.fabricmc.loom.api.processor.MinecraftJarProcessor
import net.fabricmc.loom.api.processor.ProcessorContext
import net.fabricmc.loom.api.processor.SpecContext
import net.fabricmc.tinyremapper.OutputConsumerPath
import net.fabricmc.tinyremapper.TinyRemapper
import opekope2.optigui.buildscript.extension.Version
import opekope2.optigui.buildscript.extension.commonProject
import opekope2.optigui.buildscript.task.GenerateInternalPackageInfos

plugins {
    id("opekope2.optigui.buildscript.plugin.Loader")
    alias(libs.plugins.fabric.loom)
    id("org.jetbrains.kotlin.jvm")
}

version = Version.fabric(libs.versions.optigui, libs.versions.minecraft)

val commonKotlin by configurations.registering { isCanBeResolved = true }

base {
    archivesName = "optigui-fabric"
}

repositories {
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com") { name = "Terraformers" } }
        filter { includeGroup("com.terraformersmc") }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered { officialMojangMappings(); parchment(libs.parchment) })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(libs.fabric.api)

    modApi(libs.cloth.config.fabric) { exclude(group = "net.fabricmc.fabric-api") }
    modImplementation(libs.modmenu)

    commonProject(":OptiGUI", kotlin = true)

    include(libs.ini4j)
    localRuntime(project(":ScreenAPI-Fabric", configuration = "namedElements"))
    include(project(":ScreenAPI-Fabric"))
    localRuntime(project(":ScreenNBT-Fabric", configuration = "namedElements"))
    include(project(":ScreenNBT-Fabric"))
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

    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("../run")
        }
    }
}

tasks {
    compileKotlin {
        dependsOn(commonKotlin, codegen)
        source(commonKotlin)
    }

    sourcesJar {
        dependsOn(commonKotlin)
        from(commonKotlin)
    }

    val generateInternalPackageInfos by registering(GenerateInternalPackageInfos::class) {
        sourceRoot = projectDir.resolve("src/main/kotlin")
        packageMatcher("""^opekope2\.optigui\.internal\.fabric(\..+)?$""")
    }

    codegen { dependsOn(generateInternalPackageInfos, ":OptiGUI:codegen") }

    sourceSets.main.get().java.srcDir(generateInternalPackageInfos)
    artifacts.commonJava(generateInternalPackageInfos)
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
