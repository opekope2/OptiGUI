package opekope2.optigui.filter.texture_changer

import net.minecraft.util.Identifier
import opekope2.optigui.interaction.InteractionManager
import java.util.*

/**
 * An [ITextureChanger], which changes textures to one of the textures specified in the constructor, picked randomly
 * according to the specified weights.
 *
 * @param weights The new textures and associated weights
 */
// https://www.keithschwarz.com/darts-dice-coins
class RandomizedTextureChanger(weights: Map<Identifier, Int>) : ITextureChanger {
    init {
        require(weights.isNotEmpty()) { "Weights cannot be empty" }
    }

    private val n = weights.size
    private val prob = FloatArray(n) // 1
    private val alias = IntArray(n)

    @Suppress("UNCHECKED_CAST") // filled with non-null values
    private val choices = arrayOfNulls<Identifier>(n) as Array<Identifier>
    private val random = Random(0)

    init {
        val small = LinkedList<Int>() // 2
        val large = LinkedList<Int>()
        val p = FloatArray(n)
        val totalWeight = weights.values.sumOf(Int::toLong).toDouble()
        val n = n.toLong()

        weights.onEachIndexed { i, (choice, weight) -> // 4
            choices[i] = choice
            val scaledProb = (weight * n / totalWeight).toFloat() // 3
            p[i] = scaledProb
            if (scaledProb < 1) small += i // 4.1
            else large += i // 4.2
        }

        while (small.isNotEmpty() && large.isNotEmpty()) { // 5
            val l = small.removeFirst() // 5.1
            val g = large.removeFirst() // 5.2
            prob[l] = p[l] // 5.3
            alias[l] = g // 5.4
            p[g] = (p[g] + p[l]) - 1 // 5.5
            if (p[g] < 1) small += g // 5.6
            else large += g // 5.7
        }

        while (large.isNotEmpty()) { // 6
            val g = large.removeFirst() // 6.1
            prob[g] = 1f // 6.2
        }

        while (small.isNotEmpty()) { // 7
            val l = small.removeFirst() // 7.1
            prob[l] = 1f // 7.2
        }
    }

    override fun apply(texture: Identifier) = choose(randomize(texture))

    private fun choose(random: Random): Identifier {
        val i = random.nextInt(n)
        return if (random.nextFloat() < prob[i]) choices[i]
        else choices[alias[i]]
    }

    private fun randomize(textureId: Identifier): Random {
        val seed = Objects.hash(InteractionManager.interaction, textureId).toLong() and 0xFFFFFFFFL
        return random.apply { setSeed(seed or (seed shl 32)) }
    }
}
