package opekope2.filter

import kotlin.test.*
import opekope2.filter.FilterResult.*
import opekope2.util.withResult

class FilterTests {
    private val testMatchFilter = Filter<Int, String> { Match(it.toString()) }
    private val testMismatchFilter = Filter<Int, String> { Mismatch() }
    private val testSkipFilter = Filter<Int, String> { Skip() }


    @Test
    fun matchTest() {
        val result = testMatchFilter.evaluate(1)

        assertIs<Match<*>>(result)
        assertEquals("1", (result as Match).result)
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
        val filter = NullGuardFilter(Mismatch(), testMatchFilter)

        assertIs<Mismatch<*>>(filter.evaluate(null))

        val result = filter.evaluate(1)
        assertIs<Match<*>>(result)
        assertEquals("1", (result as Match).result)
    }

    @Test
    fun nullGuardTest2() {
        val filter = NullGuardFilter(Skip(), testMismatchFilter)

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
    fun preprocessorTest() {
        val control = Filter<String, String> { Match(it) }
        val filter = PreProcessorFilter(Int::toString, control)

        assertEquals("1", (filter.evaluate(1) as Match).result)
    }

    @Test
    fun nullGuardedPreprocessorTest() {
        val control = Filter<String, String> { Match(it) }
        val filter = PreProcessorFilter.nullGuarded<Int?, String, String>({ it?.toString() }, Skip(), control)

        assertIs<Skip<*>>(filter.evaluate(null))

        val result = filter.evaluate(1)
        assertIs<Match<*>>(result)
        assertEquals("1", (result as Match).result)
    }


    @Test
    fun postprocessorTest() {
        val control = Filter<Int, Int> { Match(it) }
        val filter =
            PostProcessorFilter(control) { input, result -> result.withResult(input to (result as Match).result.toString()) }

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
