@file: JvmName("DataResultUtil")

package opekope2.optigui.util

import com.mojang.serialization.DataResult

/**
 * Extracts the result from a [DataResult] if [DataResult.isSuccess].
 *
 * @param onError Called when [DataResult.isError]
 */
inline fun <T> DataResult<T>.unwrap(onError: (DataResult.Error<T>) -> Nothing): T {
    if (isError) onError(error().get())
    return result().get()
}

/**
 * Creates a new [DataResult.error] with the message of the receiver [DataResult.Error].
 */
fun <T, U> DataResult.Error<T>.mapMessage(): DataResult<U> {
    return DataResult.error(::message)
}
