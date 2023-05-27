plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.8.10" apply false
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka", "dokka-base", "1.8.10")
    }
}

evaluationDependsOnChildren()

subprojects {
    val remapJar by project(":OptiGUI").tasks
    if (path.startsWith(":OptiGlue:")) {
        remapJar.dependsOn(tasks["remapJar"])
    }
}
