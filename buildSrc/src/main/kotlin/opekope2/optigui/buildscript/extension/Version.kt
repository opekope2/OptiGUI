package opekope2.optigui.buildscript.extension

import org.gradle.api.provider.Provider

class Version(val mod: Provider<String>, val loader: String?, val minecraft: Provider<String>) {
    override fun toString(): String {
        val mod = mod.get()
        val minecraft = minecraft.get()

        return if (loader == null) "$mod+$minecraft"
        else "$mod+$loader.$minecraft"
    }

    companion object {
        fun common(mod: Provider<String>, minecraft: Provider<String>) = Version(mod, null, minecraft)
        fun fabric(mod: Provider<String>, minecraft: Provider<String>) = Version(mod, "fabric", minecraft)
        fun neoForge(mod: Provider<String>, minecraft: Provider<String>) = Version(mod, "neoforge", minecraft)
        fun lazy(versionProvider: Provider<String>) = object {
            override fun toString() = versionProvider.get()
        }
    }
}
