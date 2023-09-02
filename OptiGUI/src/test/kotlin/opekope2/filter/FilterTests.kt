package opekope2.filter

import opekope2.optigui.filter.*
import opekope2.optigui.filter.IFilter.Result.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FilterTests {
    private val testMatchFilter = IFilter<Int, String> { value -> match(value.toString()) }
    private val testMismatchFilter = IFilter<Int, String> { mismatch() }
    private val testSkipFilter = IFilter<Int, String> { skip() }


    @Test
    fun matchTest() {
        val result = testMatchFilter.evaluate(1)

        assertIs<Match<*>>(result)
        assertEquals("1", (result as Match).result)
    }


    @Test
    fun conditionalTest() {
        assertIs<Mismatch<*>>(ConditionalFilter({ false }, mismatch(), testMatchFilter).evaluate(0))
        assertIs<Match<*>>(ConditionalFilter({ true }, mismatch(), testMatchFilter).evaluate(0))
    }


    @Test
    fun conjunctionMatchTest() {
        assertIs<Match<*>>(ConjunctionFilter(testMatchFilter).evaluate(0))
        assertIs<Match<*>>(ConjunctionFilter(testMatchFilter, testSkipFilter).evaluate(0))
    }

    @Test
    fun conjunctionMismatchTest() {
        assertIs<Mismatch<*>>(ConjunctionFilter(testMismatchFilter).evaluate(0))
        assertIs<Mismatch<*>>(ConjunctionFilter(testMatchFilter, testMismatchFilter).evaluate(0))
        assertIs<Mismatch<*>>(ConjunctionFilter(testMatchFilter, testSkipFilter, testMismatchFilter).evaluate(0))
        assertIs<Mismatch<*>>(ConjunctionFilter(testMismatchFilter, testMatchFilter).evaluate(0))
    }

    @Test
    fun conjunctionSkipTest() {
        assertIs<Skip<*>>(ConjunctionFilter<Int>().evaluate(0))
        assertIs<Skip<*>>(ConjunctionFilter(testSkipFilter).evaluate(0))
        assertIs<Skip<*>>(ConjunctionFilter(testSkipFilter, testSkipFilter).evaluate(0))
    }


    @Test
    fun firstMatchSkipTest() {
        assertIs<Skip<*>>(FirstMatchFilter(testSkipFilter).evaluate(1))
        assertIs<Skip<*>>(FirstMatchFilter(testSkipFilter, testSkipFilter).evaluate(1))
    }

    @Test
    fun firstMatchMismatchTest() {
        assertIs<Mismatch<*>>(FirstMatchFilter(testMismatchFilter).evaluate(1))
        assertIs<Mismatch<*>>(FirstMatchFilter(testSkipFilter, testMismatchFilter).evaluate(1))
    }

    @Test
    fun firstMatchMatchTest1() {
        assertIs<Match<*>>(FirstMatchFilter(testMatchFilter).evaluate(1))
        assertIs<Match<*>>(FirstMatchFilter(testSkipFilter, testMismatchFilter, testMatchFilter).evaluate(1))
    }

    @Test
    fun firstMatchMatchTest2() {
        val matchFilter = IFilter<Int, String> { value -> match((-value).toString()) }
        var res = FirstMatchFilter(testMatchFilter, matchFilter).evaluate(1)

        assertIs<Match<*>>(res)
        assertEquals("1", res.result)

        res = FirstMatchFilter(matchFilter, testMatchFilter).evaluate(1)

        assertIs<Match<*>>(res)
        assertEquals("-1", res.result)
    }


    @Test
    fun containingTest() {
        val filter = ContainingFilter((1..5).toSet())

        assertIs<Mismatch<*>>(filter.evaluate(0))
        assertIs<Match<*>>(filter.evaluate(1))
    }


    @Test
    fun disjunctionMatchTest() {
        assertIs<Match<*>>(ConjunctionFilter(testMatchFilter).evaluate(0))
        assertIs<Mismatch<*>>(ConjunctionFilter(testMatchFilter, testMismatchFilter).evaluate(0))
        assertIs<Mismatch<*>>(ConjunctionFilter(testMatchFilter, testSkipFilter, testMismatchFilter).evaluate(0))
        assertIs<Match<*>>(ConjunctionFilter(testMatchFilter, testSkipFilter).evaluate(0))
    }

    @Test
    fun disjunctionMismatchTest() {
        assertIs<Mismatch<*>>(ConjunctionFilter(testMismatchFilter).evaluate(0))
        assertIs<Mismatch<*>>(ConjunctionFilter(testMismatchFilter, testMatchFilter).evaluate(0))
    }

    @Test
    fun disjunctionSkipTest() {
        assertIs<Skip<*>>(ConjunctionFilter<Int>().evaluate(0))
        assertIs<Skip<*>>(ConjunctionFilter(testSkipFilter).evaluate(0))
        assertIs<Skip<*>>(ConjunctionFilter(testSkipFilter, testSkipFilter).evaluate(0))
    }


    @Test
    fun equalityTest() {
        val filter = EqualityFilter(1)

        assertIs<Mismatch<*>>(filter.evaluate(0))
        assertIs<Match<*>>(filter.evaluate(1))
    }


    @Test
    fun inequalityTest() {
        val filter = InequalityFilter(0)

        assertIs<Mismatch<*>>(filter.evaluate(0))
        assertIs<Match<*>>(filter.evaluate(1))
    }


    @Test
    fun negationTest1() {
        val filter = NegationFilter(testMatchFilter)

        assertIs<Mismatch<*>>(filter.evaluate(0))
    }

    @Test
    fun negationTest2() {
        val filter = NegationFilter(testMismatchFilter)

        assertIs<Match<*>>(filter.evaluate(0))
    }

    @Test
    fun negationTest3() {
        val filter = NegationFilter(testSkipFilter)

        assertIs<Skip<*>>(filter.evaluate(0))
    }


    @Test
    fun nullGuardTest1() {
        val filter = NullGuardFilter(mismatch(), testMatchFilter)

        assertIs<Mismatch<*>>(filter.evaluate(null))

        val result = filter.evaluate(1)
        assertIs<Match<*>>(result)
        assertEquals("1", (result as Match).result)
    }

    @Test
    fun nullGuardTest2() {
        val filter = NullGuardFilter(skip(), testMismatchFilter)

        assertIs<Skip<*>>(filter.evaluate(null))
        assertIs<Mismatch<*>>(filter.evaluate(1))
    }


    @Test
    fun numberRangeTest1() {
        val filter = RangeFilter.atLeast(5)

        assertIs<Match<*>>(filter.evaluate(10))
        assertIs<Mismatch<*>>(filter.evaluate(0))
    }

    @Test
    fun numberRangeTest2() {
        val filter = RangeFilter.atMost(5)

        assertIs<Match<*>>(filter.evaluate(0))
        assertIs<Mismatch<*>>(filter.evaluate(10))
    }

    @Test
    fun numberRangeTest3() {
        val filter = RangeFilter.between(1, 10)

        assertIs<Match<*>>(filter.evaluate(5))
        assertIs<Mismatch<*>>(filter.evaluate(0))
    }


    @Test
    fun optionalTest() {
        val filter = OptionalFilter(mismatch(), testMatchFilter)

        assertIs<Mismatch<*>>(filter.evaluate(Optional.empty()))
        assertIs<Match<*>>(filter.evaluate(Optional.of(1)))
    }


    @Test
    fun preprocessorTest() {
        val control = IFilter<String, String> { value -> match(value) }
        val filter = PreProcessorFilter(Int::toString, control)

        assertEquals("1", (filter.evaluate(1) as Match).result)
    }

    @Test
    fun nullGuardedPreprocessorTest() {
        val control = IFilter<String, String> { value -> match(value) }
        val filter = PreProcessorFilter.nullGuarded<Int?, String, String>({ it?.toString() }, skip(), control)

        assertIs<Skip<*>>(filter.evaluate(null))

        val result = filter.evaluate(1)
        assertIs<Match<*>>(result)
        assertEquals("1", (result as Match).result)
    }


    @Test
    fun postprocessorTest() {
        val control = IFilter<Int, Int> { value -> match(value) }
        val filter =
            PostProcessorFilter(control) { input, result -> match(input to (result as Match).result.toString()) }

        val result = filter.evaluate(1)
        assertIs<Match<*>>(result)
        assertEquals(1 to "1", (result as Match).result)
    }


    @Test
    fun regularExpressionTest() {
        val filter = RegularExpressionFilter("^1$".toRegex())

        assertIs<Match<*>>(filter.evaluate("1"))
        assertIs<Mismatch<*>>(filter.evaluate("11"))
    }
}
