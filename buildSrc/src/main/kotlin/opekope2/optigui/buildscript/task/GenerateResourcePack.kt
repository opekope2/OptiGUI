package opekope2.optigui.buildscript.task

import com.github.holgerbrandl.jsonbuilder.json
import opekope2.optigui.buildscript.builder.ResourcePackBuilder
import opekope2.optigui.buildscript.util.IDestination
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.listProperty
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.jar.JarFile
import javax.imageio.ImageIO

abstract class GenerateResourcePack : DefaultTask(), IDestination<Directory> {
    @get: InputFile
    abstract var minecraftJar: File

    @Optional
    @OutputDirectory
    override val destination: DirectoryProperty = project.objects.directoryProperty()

    @Nested
    val resourcePacksToGenerate: ListProperty<ResourcePackBuilder> =
        project.objects.listProperty<ResourcePackBuilder>().convention(emptyList())

    fun generate(action: ResourcePackBuilder.() -> Unit) {
        resourcePacksToGenerate.add(ResourcePackBuilder(project).apply(action))
    }

    @TaskAction
    fun run() {
        resourcePacksToGenerate.get().forEach {
            if (it.properties.isPresent == it.ini.isPresent) {
                throw IllegalStateException("Either properties or ini is required")
            }
            if (it.test.block.isPresent == it.test.entity.isPresent) {
                throw IllegalStateException("Either block or entity is required")
            }
            if (!it.destination.get().startsWith("assets/")) {
                throw IllegalStateException("Destination must be in the assets/ directory")
            }
            if (it.destination.get().count { char -> char == '/' } <= 2) {
                throw IllegalStateException("Destination must not be directly in the assets/ directory")
            }
        }

        JarFile(minecraftJar).use { jar ->
            resourcePacksToGenerate.get().forEach { packBuilder ->
                val source = packBuilder.source.get()
                val destination = packBuilder.destination.get()
                val properties = packBuilder.properties
                val ini = packBuilder.ini
                val test = packBuilder.test

                val containerId =
                    if (test.block.isPresent) test.block.get().id.get()
                    else test.entity.get().id.get()
                assert(destination.startsWith("assets/"))
                val replacementId = destination.substring("assets/".length).replaceFirst('/', ':')


                val outRelativePath = (properties.orNull ?: ini.orNull)!!.destination.get()
                val outFile = this.destination.file(outRelativePath).get().asFile.absoluteFile
                outFile.parentFile.mkdirs()

                val resourceText = when {
                    properties.isPresent -> {
                        // Generate properties
                        val generated = properties.get().build(replacementId)

                        generated.store(outFile)
                        StringWriter().also(generated::store).toString()
                    }

                    ini.isPresent -> {
                        // Generate INI
                        val generated = ini.get().build(containerId, replacementId)

                        generated.store(outFile)
                        StringWriter().also(generated::store).toString()
                    }

                    else -> {
                        throw AssertionError("Both properties and ini was missing! This shouldn't have happened!")
                    }
                }


                // Generate replacement texture
                val imageOutFile = this.destination.file(destination).get().asFile.absoluteFile
                val sourceImage = jar.getInputStream(jar.getJarEntry(source)).use(ImageIO::read)
                sourceImage.createGraphics().apply {
                    color = Color.BLACK
                    font = Font(Font.MONOSPACED, Font.PLAIN, 8)
                    drawMultilineString(resourceText, 0, font.size)
                }

                imageOutFile.parentFile.mkdirs()
                assert(ImageIO.write(sourceImage, "png", imageOutFile)) { "Failed to write PNG" }


                // Generate test descriptor
                val generated = test.build(replacementId)

                val testOutFile = this.destination.get().file(test.destination.get()).asFile.absoluteFile
                testOutFile.parentFile.mkdirs()
                PrintWriter(testOutFile).use(generated::write)


                // Generate pack.mcmeta
                val packMcmetaFile = this.destination.file("pack.mcmeta").get().asFile
                val packMcmeta = json {
                    "pack" to {
                        "pack_format" to 22
                        "description" to "OptiGUI test resource pack"
                    }
                }
                PrintWriter(packMcmetaFile).use(packMcmeta::write)
            }
        }
    }

    private fun Graphics.drawMultilineString(text: String, x: Int, y: Int) {
        val strings = text.split('\n')
        var cy = y
        for (string in strings) {
            drawString(string, x, cy)
            cy += fontMetrics.height
        }
    }
}
