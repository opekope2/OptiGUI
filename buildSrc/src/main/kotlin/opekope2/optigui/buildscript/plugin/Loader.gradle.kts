package opekope2.optigui.buildscript.plugin

plugins {
    `java-library`
}

val commonJava by configurations.registering { isCanBeResolved = true }
val commonResources by configurations.registering { isCanBeResolved = true }

tasks {
    compileJava {
        dependsOn(commonJava)
        source(commonJava)
    }

    processResources {
        dependsOn(commonResources)
        from(commonResources)
    }

    named<Jar>("sourcesJar") {
        dependsOn(commonJava, commonResources)
        from(commonJava, commonResources)
    }
}
