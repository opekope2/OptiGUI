base {
    archivesName = "optigui-screen-api"
}

dependencies {
    modImplementation(libs.fabric.loader)
}

tasks {
    jar {
        from("LICENSE")
        exclude("COPYING", "COPYING.LESSER")
    }
}
