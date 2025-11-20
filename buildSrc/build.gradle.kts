plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.jvm.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location)) // https://stackoverflow.com/a/77181831
}
