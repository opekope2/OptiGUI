package opekope2.optigui.buildscript.extension

import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.commonProject(path: String, kotlin: Boolean = false) {
    "compileOnly"(project(path))
    "commonJava"(project(path, configuration = "commonJava"))
    if (kotlin) "commonKotlin"(project(path, configuration = "commonKotlin"))
    "commonResources"(project(path, configuration = "commonResources"))
}
