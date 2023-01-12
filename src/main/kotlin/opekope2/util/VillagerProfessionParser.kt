package opekope2.util

import net.minecraft.util.Identifier

/**
 * Parses a villager profession compatible with [OptiFine docs](https://optifine.readthedocs.io/custom_guis.html#villagers).
 * In fact, it parses a superset (additional features): a namespace is optionally accepted in front.
 */
fun parseProfession(input: String): Pair<Identifier, Collection<NumberOrRange>>? {
    val parts = input.split(':')

    var namespace = Identifier.DEFAULT_NAMESPACE
    val profession: String
    var levels: Collection<NumberOrRange>? = null

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

    return Pair(Identifier(namespace, profession), levels ?: listOf())
}

private fun parseRangeList(rangeList: String) = rangeList.split(',').mapNotNull { NumberOrRange.parse(it) }
