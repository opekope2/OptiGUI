package opekope2.optigui.internal.selector

import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.*
import opekope2.optigui.properties.IIndependentProperties
import opekope2.util.*
import java.time.Month


@Selector("date")
class DateSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? {
        return selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map({ date ->
                if ('@' in date) {
                    if (date.count { it == '@' } > 2) return@map null
                    val (monthName, day) = date.split('@')
                    val (month) = monthUnmapping.firstOrNull { (_, aliases) -> monthName in aliases }
                        ?: return@map null
                    month to (NumberOrRange.tryParse(day) ?: return@map null)
                } else {
                    val (month) = monthUnmapping.firstOrNull { (_, aliases) -> date in aliases } ?: return@map null
                    month to null
                }
            }) { throw RuntimeException("Invalid dates: ${joinNotFound(it)}") }
            ?.assertNotEmpty()
            ?.let { dates ->
                DisjunctionFilter(
                    dates.map { (month, day) ->
                        val monthFilter = PreProcessorFilter.nullGuarded<Interaction, Month, Unit>(
                            { (it.data as? IIndependentProperties)?.date?.month },
                            IFilter.Result.mismatch(),
                            EqualityFilter(month)
                        )
                        val dayFilter = day?.toFilter()

                        if (dayFilter == null) monthFilter
                        else ConjunctionFilter(
                            monthFilter,
                            PreProcessorFilter.nullGuarded(
                                { (it.data as? IIndependentProperties)?.date?.dayOfMonth },
                                IFilter.Result.mismatch(),
                                dayFilter
                            )
                        )
                    }
                )
            }
    }

    private companion object {
        private val monthUnmapping = arrayOf(
            Month.JANUARY to arrayOf("jan", "january", "1"),
            Month.FEBRUARY to arrayOf("feb", "february", "2"),
            Month.MARCH to arrayOf("mar", "march", "3"),
            Month.APRIL to arrayOf("apr", "april", "4"),
            Month.MAY to arrayOf("may", "5"),
            Month.JUNE to arrayOf("jun", "june", "6"),
            Month.JULY to arrayOf("jul", "july", "7"),
            Month.AUGUST to arrayOf("aug", "augustus", "8"),
            Month.SEPTEMBER to arrayOf("sep", "september", "9"),
            Month.OCTOBER to arrayOf("oct", "october", "spooktober", "10"),
            Month.NOVEMBER to arrayOf("nov", "november", "11"),
            Month.DECEMBER to arrayOf("dec", "december", "12")
        )
    }
}
