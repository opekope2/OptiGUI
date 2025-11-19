package opekope2.optigui.buildscript.plugin

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
}

val libs = the<LibrariesForLibs>() // https://stackoverflow.com/a/77181831

val commonJava by configurations.registering { isCanBeResolved = false; isCanBeConsumed = true }
val commonResources by configurations.registering { isCanBeResolved = false; isCanBeConsumed = true }

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common) // Fabric and NeoForge both bundle MixinExtras, so it is safe to use it in common
    annotationProcessor(libs.mixinextras.common)
}

artifacts {
    sourceSets.main {
        java.sourceDirectories.forEach { add("commonJava", it) }
        resources.sourceDirectories.forEach { add("commonResources", it) }
    }
}
