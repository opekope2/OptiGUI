import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

fun Project.gradleProperty(name: String) = extra[name].toString()
