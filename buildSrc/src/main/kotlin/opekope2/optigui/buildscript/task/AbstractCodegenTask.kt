package opekope2.optigui.buildscript.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory

abstract class AbstractCodegenTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        outputDir.convention(project.layout.buildDirectory.dir("generated/$name"))
    }

    protected fun deleteOutputDir() {
        outputDir.get().asFile.deleteRecursively()
    }
}
