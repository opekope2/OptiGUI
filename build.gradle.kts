plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka", "dokka-base", project.gradleProperty("dokka_version"))
    }
}
