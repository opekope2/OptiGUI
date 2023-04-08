@file: JvmName("VillagerProfessionParser")

package opekope2.util

import net.minecraft.util.Identifier

/**
 * Parses a villager profession according to the [OptiGUI docs](https://opekope2.github.io/OptiGUI-Next/format/#villagers).
 */
fun tryParseProfession(input: String): Pair<Identifier, Collection<NumberOrRange>>? {
    val parts = input.split(':')

    var namespace = Identifier.DEFAULT_NAMESPACE
    val profession: String
    var levels: Collection<OldNumberOrRange>? = null

    when (parts.size) {
        1 -> {
            profession = parts[0]
        }

        2 -> {
            // Second part starts with a number?
            if (parts[1][0] in '0'..'9') {
                profession = parts[0]
                levels = parseRangeList(parts[1])
            } else {
                namespace = parts[0]
                profession = parts[1]
            }
        }

        3 -> {
            namespace = parts[0]
            profession = parts[1]
            levels = parseRangeList(parts[2])
        }

        else -> {
            return null
        }
    }

    return Identifier(namespace, profession) to (levels ?: listOf())
}

private fun parseRangeList(rangeList: String) =
    rangeList.splitIgnoreEmpty(',').mapNotNull(OldNumberOrRange::tryParse)
