package opekope2.util

fun <T> catchAll(function: () -> T): T? = try {
    function()
} catch (_: Throwable) {
    null
}
