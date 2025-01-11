@file: JvmName("DataResultUtil")

package opekope2.optigui.util

import com.mojang.serialization.DataResult

/**
 * Extracts the result from a [DataResult] if [DataResult.result] is present.
 *
 * @param onError Called when [DataResult.error] is present
 */
inline fun <T> DataResult<T>.unwrap(onError: (DataResult<T>) -> Nothing): T {
    if (error().isPresent) onError(this)
    return result().get()
}
