package opekope2.optigui.buildscript.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.nio.file.Files
import kotlin.io.path.isDirectory
import kotlin.io.path.relativeTo

abstract class GenerateInternalPackageInfos : DefaultTask() {
    init {
        outputDir.convention(project.layout.buildDirectory.dir("generated/$name"))
    }

    @get:Input
    abstract val matchPackages: Property<Regex>

    @get:SkipWhenEmpty
    @get:InputDirectory
    abstract val sourceRoot: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        outputDir.get().asFile.deleteRecursively()
        val root = sourceRoot.get().asFile.toPath()

        for (path in Files.walk(root)) {
            if (!path.isDirectory()) continue

            val dir = path.relativeTo(root).toString()
            val packageName = dir.replace(path.fileSystem.separator, ".")
            if (!matchPackages.get().matches(packageName)) continue

            generatePackageInfo(dir, packageName)
        }
    }

    private fun generatePackageInfo(outDir: String, packageName: String) {
        val packageDir = outputDir.dir(outDir).get()
        val packageInfo = packageDir.file("package-info.java")
        packageDir.asFile.mkdirs()

        val content = """
            @ApiStatus.Internal
            package $packageName;
            
            import org.jetbrains.annotations.ApiStatus;
        """.trimIndent()
        packageInfo.asFile.writeText(content)
    }
}
