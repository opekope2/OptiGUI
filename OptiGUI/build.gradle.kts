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
        langFile = projectDir.resolve("src/main/resources/assets/optigui/lang/en_us.json")
        packageName = "opekope2.optigui.internal"
    }

    val generateInternalPackageInfos by registering(GenerateInternalPackageInfos::class) {
        sourceRoot = projectDir.resolve("src/main/kotlin")
        matchPackages = """^opekope2\.optigui\.internal(\..+)?$""".toRegex()
    }

    named("compileKotlin") { dependsOn(generateI18n, generateInternalPackageInfos) }
    named("sourcesJar") { dependsOn(generateI18n, generateInternalPackageInfos) }

    sourceSets.main {
        java.srcDir(generateInternalPackageInfos)
        kotlin.srcDir(generateI18n)
    }
}
