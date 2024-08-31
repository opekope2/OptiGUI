package opekope2.optigui.internal.resource.loader.sdlang

import com.google.common.collect.ImmutableList
import com.singingbush.sdl.Tag

internal class SDLangDocument(val tags: List<Tag>) {
    val constants = mutableMapOf<String, ImmutableList<Any>>()
}
