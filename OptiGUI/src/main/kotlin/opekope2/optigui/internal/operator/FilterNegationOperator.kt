package opekope2.optigui.internal.operator

import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.matchNot

internal object FilterNegationOperator : ISubFilterNbtOperator {
    override fun createFilter(subFilter: INbtFilter) = matchNot(subFilter)
}
