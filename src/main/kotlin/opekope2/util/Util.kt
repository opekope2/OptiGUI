package opekope2.util

import opekope2.filter.FilterResult

fun <T> catchAll(function: () -> T): T? = try {
    function()
} catch (_: Throwable) {
    null
}
