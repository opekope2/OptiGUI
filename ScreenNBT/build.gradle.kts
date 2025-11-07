base {
    archivesName = "optigui-screen-nbt"
}

dependencies {
    modImplementation(libs.fabric.loader)
    implementation(project(":ScreenAPI", configuration = "namedElements"))
}
