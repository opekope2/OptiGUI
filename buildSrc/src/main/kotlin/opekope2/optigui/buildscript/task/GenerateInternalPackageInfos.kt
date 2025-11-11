package opekope2.optigui.buildscript.task

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.nio.file.Files
import kotlin.io.path.isDirectory
import kotlin.io.path.relativeTo

@CacheableTask
abstract class GenerateInternalPackageInfos : AbstractCodegenTask() {
    @get:Input
    abstract val packageMatcher: Property<Regex>

    @get:SkipWhenEmpty
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceRoot: DirectoryProperty

    fun packageMatcher(packageMatcher: String) {
        this.packageMatcher.set(packageMatcher.toRegex())
    }

    @TaskAction
    fun run() {
        deleteOutputDir()

        val packageMatcher = packageMatcher.get()
        val sourceRoot = sourceRoot.asFile.get().toPath()

        for (path in Files.walk(sourceRoot)) {
            if (!path.isDirectory()) continue

            val dir = path.relativeTo(sourceRoot).toString()
            val packageName = dir.replace(path.fileSystem.separator, ".")
            if (!packageMatcher.matches(packageName)) continue

            generatePackageInfo(dir, packageName)
        }
    }

    private fun generatePackageInfo(outDir: String, packageName: String) {
        val packageDir = outputDir.dir(outDir).get()
        val packageInfo = packageDir.file("package-info.java")
        packageDir.asFile.mkdirs()

        //language=java
        val content = """
            @ApiStatus.Internal
            package $packageName;
            
            import org.jetbrains.annotations.ApiStatus;
        """.trimIndent()
        packageInfo.asFile.writeText(content)
    }
}
