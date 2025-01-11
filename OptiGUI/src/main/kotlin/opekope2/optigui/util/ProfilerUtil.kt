@file: JvmName("ProfilerUtil")

package opekope2.optigui.util

import net.minecraft.util.profiler.Profiler

/**
 * [Pushes][Profiler.push] to the given profiler, invokes [action], then [pops][Profiler.pop] from the given profiler.
 *
 * @param location The path in the profiler
 * @param action The action to invoke between [Profiler.push] and [Profiler.pop]
 * @return The value returned by [action]
 */
inline fun <T> Profiler.push(location: String, action: Profiler.() -> T): T {
    push(location)
    val result = action()
    pop()
    return result
}
