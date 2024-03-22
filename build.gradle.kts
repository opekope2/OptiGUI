plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath(libs.dokka.base)
    }
}
