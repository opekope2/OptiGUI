package opekope2.optigui.filter.comparer

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.*
import net.minecraft.util.ExtraCodecs
import opekope2.optigui.filter.ConstantNbtComparerFilter
import opekope2.optigui.filter.DynamicNbtComparerFilter
import opekope2.optigui.internal.I18n
import java.util.*

/**
 * An NBT comparer, which compares NBT strings or NBT numbers with each other.
 *
 * @param ignoreCase Whether to ignore case when comparing NBT strings
 */
sealed class NbtStringOrNumberComparer(val ignoreCase: Boolean) : INbtComparer {
    /**
     * Creates a type describing a [ConstantNbtComparerFilter] with this comparer.
     *
     * @param acceptedResults The accepted results of a comparison
     */
    fun constantType(acceptedResults: EnumSet<INbtComparer.ComparisonResult>) =
        ConstantNbtComparerFilter.Type(this, acceptedResults, nbtStringOrNumberCodec)

    /**
     * Creates a type describing a [ConstantNbtComparerFilter] with this comparer.
     *
     * @param acceptedResults The accepted results of a comparison
     */
    fun constantType(vararg acceptedResults: INbtComparer.ComparisonResult) =
        constantType(acceptedResults.toCollection(EnumSet.noneOf(INbtComparer.ComparisonResult::class.java)))

    /**
     * Creates a type describing a [DynamicNbtComparerFilter] with this comparer.
     *
     * @param acceptedResults The accepted results of a comparison
     */
    fun dynamicType(acceptedResults: EnumSet<INbtComparer.ComparisonResult>) =
        DynamicNbtComparerFilter.Type(this, acceptedResults)

    /**
     * Creates a type describing a [DynamicNbtComparerFilter] with this comparer.
     *
     * @param acceptedResults The accepted results of a comparison
     */
    fun dynamicType(vararg acceptedResults: INbtComparer.ComparisonResult) =
        dynamicType(acceptedResults.toCollection(EnumSet.noneOf(INbtComparer.ComparisonResult::class.java)))

    override fun compare(nbt: Tag, reference: Tag): INbtComparer.ComparisonResult {
        return INbtComparer.ComparisonResult.ofComparison(
            when {
                nbt is StringTag && reference is StringTag -> reference.asString.compareTo(nbt.asString, ignoreCase)
                nbt !is NumericTag || reference !is NumericTag -> return INbtComparer.ComparisonResult.INCOMPARABLE
                nbt is DoubleTag || reference is DoubleTag -> reference.asDouble.compareTo(nbt.asDouble)
                nbt is FloatTag || reference is FloatTag -> reference.asFloat.compareTo(nbt.asFloat)
                nbt is LongTag || reference is LongTag -> reference.asLong.compareTo(nbt.asLong)
                else -> nbt.asInt.compareTo(reference.asInt)
            }
        )
    }

    /**
     * A case-sensitive variant of [NbtStringOrNumberComparer].
     */
    data object CaseSensitive : NbtStringOrNumberComparer(false)

    /**
     * A case-insensitive variant of [NbtStringOrNumberComparer].
     */
    data object CaseInsensitive : NbtStringOrNumberComparer(true)

    private companion object {
        private val nbtStringOrNumberCodec: Codec<Tag> = ExtraCodecs.converter(NbtOps.INSTANCE).validate {
            if (it is StringTag || it is NumericTag) DataResult.success(it)
            else DataResult.error { I18n.OPTIGUI_VALIDATION_ERROR_NOT_A_NUMBER_OR_STRING.getTranslation(it.asString) }
        }
    }
}
