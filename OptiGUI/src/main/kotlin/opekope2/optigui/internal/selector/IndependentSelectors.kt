package opekope2.optigui.internal.selector

import kotlinx.datetime.*
import opekope2.optigui.filter.ConjunctionFilter
import opekope2.optigui.filter.DisjunctionFilter
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.util.NumberOrRange

internal class DateSelector : AbstractListSelector<Pair<Month, NumberOrRange?>>() {
    private val today: LocalDate
        get() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    override fun parseSelector(selector: String): Pair<Month, NumberOrRange?>? {
        val parts = selector.split('@')
        if (parts.size != 2) return null
        val (rawMonth, rawDay) = parts

        val month = getMonth(rawMonth) ?: return null
        val day = NumberOrRange.tryParse(rawDay)

        return month to day
    }

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid dates: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Pair<Month, NumberOrRange?>>) = DisjunctionFilter(
        parsedSelectors.map { (month, day) ->
            val monthFilter = PreProcessorFilter.nullGuarded<Interaction, Month, Unit>(
                { today.month },
                "Get month",
                null,
                EqualityFilter(month)
            )
            val dayFilter = day?.toFilter() ?: return@map monthFilter

            ConjunctionFilter(
                monthFilter,
                PreProcessorFilter.nullGuarded(
                    { today.dayOfMonth },
                    "Get day of month",
                    null,
                    dayFilter
                )
            )
        }
    )

    override fun transformInteraction(interaction: Interaction): String {
        val today = today
        return "${today.month.name.lowercase()}@${today.dayOfMonth}"
    }

    private fun getMonth(monthName: String) = when (monthName) {
        "jan", "january", "1" -> Month.JANUARY
        "feb", "february", "2" -> Month.FEBRUARY
        "mar", "march", "3" -> Month.MARCH
        "apr", "april", "4" -> Month.APRIL
        "may", "5" -> Month.MAY
        "jun", "june", "6" -> Month.JUNE
        "jul", "july", "7" -> Month.JULY
        "aug", "augustus", "8" -> Month.AUGUST
        "sep", "september", "9" -> Month.SEPTEMBER
        "oct", "october", "spooktober", "10" -> Month.OCTOBER
        "nov", "november", "11" -> Month.NOVEMBER
        "dec", "december", "12" -> Month.DECEMBER
        else -> null
    }
}
