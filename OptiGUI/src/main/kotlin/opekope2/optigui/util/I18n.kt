package opekope2.optigui.util

import net.minecraft.text.Text

/**
 * @see Text.translatableWithFallback
 */
fun i18n(key: String, fallback: String, vararg args: Any?): String =
    Text.translatableWithFallback(key, fallback, *args).string
