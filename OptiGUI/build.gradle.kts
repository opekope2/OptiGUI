import opekope2.optigui.buildscript.task.GenerateI18nEnum
import opekope2.optigui.buildscript.task.GenerateInternalPackageInfos

plugins {
    alias(libs.plugins.kotlin.jvm)
}

architectury {
    injectInjectables = false
    common("fabric")
}

repositories {
    maven("https://maven.shedaniel.me") { name = "Shedaniel" }
}

dependencies {
    api(libs.ini4j)
    modApi(libs.cloth.config.fabric) { exclude(group = "net.fabricmc.fabric-api") }

    api(project(":ScreenAPI", configuration = "namedElements"))
}

tasks {
    val generateI18n by registering(GenerateI18nEnum::class) {
        inputs.file(projectDir.resolve("src/main/resources/assets/optigui/lang/en_us.json"))
        enumPackage = "opekope2.optigui.internal"
    }

    val generateInternalPackageInfos by registering(GenerateInternalPackageInfos::class) {
        sourceRoot = projectDir.resolve("src/main/kotlin")
        packageMatcher("""^opekope2\.optigui\.internal(\..+)?$""")
    }

    codegen { dependsOn(generateI18n, generateInternalPackageInfos) }

    sourceSets.main {
        java.srcDirs(generateInternalPackageInfos)
        kotlin.srcDirs(generateI18n)
    }
}
