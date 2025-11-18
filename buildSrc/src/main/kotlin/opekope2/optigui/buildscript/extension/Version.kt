package opekope2.optigui.buildscript.extension

import org.gradle.api.provider.Provider

object Version {
    fun of(mod: Provider<String>, loader: String?, minecraft: Provider<String>) =
        of(mod.get(), loader, minecraft.get())

    fun of(mod: String, loader: String?, minecraft: String) =
        if (loader == null) "$mod+$minecraft"
        else "$mod+$loader.$minecraft"

    fun common(mod: Provider<String>, minecraft: Provider<String>) = of(mod, null, minecraft)
    fun common(mod: String, minecraft: String) = of(mod, null, minecraft)
    fun fabric(mod: Provider<String>, minecraft: Provider<String>) = of(mod, "fabric", minecraft)
    fun fabric(mod: String, minecraft: String) = of(mod, "fabric", minecraft)
    fun neoForge(mod: Provider<String>, minecraft: Provider<String>) = of(mod, "neoforge", minecraft)
    fun neoForge(mod: String, minecraft: String) = of(mod, "neoforge", minecraft)
}
