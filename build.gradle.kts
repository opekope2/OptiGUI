import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.processor.MinecraftJarProcessor
import net.fabricmc.loom.api.processor.ProcessorContext
import net.fabricmc.loom.api.processor.SpecContext
import net.fabricmc.loom.util.TinyRemapperLoggerAdapter
import net.fabricmc.tinyremapper.OutputConsumerPath
import net.fabricmc.tinyremapper.TinyRemapper
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.architectury)
    alias(libs.plugins.shadow) apply false
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

allprojects {
    val loaderSuffix = if (extra.has("loom.platform")) "${extra["loom.platform"]}." else ""
    val minecraftVersion = rootProject.libs.versions.minecraft.get()
    val modVersion = rootProject.libs.versions.optigui.get()
    group = "opekope2.optigui"
    version = "$modVersion+$loaderSuffix$minecraftVersion"
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.loom.get().pluginId)
    apply(plugin = rootProject.libs.plugins.architectury.get().pluginId)

    base {
        archivesName = "optigui"
    }

    repositories {
    }

    val jetbrainsToJsr305 = mapOf(
        "org/jetbrains/annotations/Nullable" to "javax/annotation/Nullable",
        "org/jetbrains/annotations/NotNull" to "javax/annotation/Nonnull",
        "org/jetbrains/annotations/Unmodifiable" to "javax/annotation/concurrent/Immutable"
    )
    project.extensions.configure<LoomGradleExtensionAPI>("loom") {
        runtimeOnlyLog4j = true
        silentMojangMappingsLicense()
        // Unfuck MethodsReturnNonnullByDefault
        addMinecraftJarProcessor(RemappingJarProcessor::class.java, "optigui:jsr305-annotations", jetbrainsToJsr305)
    }

    dependencies {
        "minecraft"(rootProject.libs.minecraft)
        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").layered {
            officialMojangMappings()
            parchment(rootProject.libs.parchment)
        })
        api(rootProject.libs.jspecify)
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.toVersion(rootProject.libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(rootProject.libs.versions.java.get())
        toolchain.languageVersion = JavaLanguageVersion.of(rootProject.libs.versions.java.get())
    }

    tasks {
        withType<JavaCompile>().configureEach {
            sourceCompatibility = rootProject.libs.versions.java.get()
            targetCompatibility = rootProject.libs.versions.java.get()
            options.release = rootProject.libs.versions.java.get().toInt()
            options.encoding = "UTF-8"
        }

        withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget = JvmTarget.fromTarget(rootProject.libs.versions.java.get())
                freeCompilerArgs.addAll("-Xjvm-default=all", "-Xjsr305=strict")
            }
        }

        jar {
            from(rootDir.resolve("COPYING"))
            from(rootDir.resolve("COPYING.LESSER"))
        }

        processResources {
            val properties = mapOf(
                "version" to version as String,
                "fabric_loader" to libs.versions.fabric.loader.get(),
                "fabric_api" to libs.versions.fabric.api.get(),
                "fabric_language_kotlin" to libs.versions.fabric.language.kotlin.get(),
                "minecraft" to libs.versions.minecraft.get(),
                "java" to libs.versions.java.get(),
                "cloth_config" to libs.versions.cloth.config.fabric.get(),
            )

            filesMatching("fabric.mod.json") { expand(properties) }
            filesMatching("*.mixins.json") { expand(properties) }
        }

        val codegen by registering
        named("compileJava") { dependsOn(codegen) }
        named("compileKotlin") { dependsOn(codegen) }
        named("sourcesJar") { dependsOn(codegen) }
    }
}


abstract class RemappingJarProcessor @Inject constructor(
    private val name: String,
    private val mappings: Map<String, String>
) : MinecraftJarProcessor<RemappingJarProcessor.Spec> {
    override fun buildSpec(context: SpecContext?) = Spec(mappings)

    override fun processJar(jar: java.nio.file.Path, spec: Spec, context: ProcessorContext?) {
        val tinyRemapper: TinyRemapper = TinyRemapper.newRemapper(TinyRemapperLoggerAdapter.INSTANCE)
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
