package opekope2.filter

import opekope2.optigui.filter.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FilterTests {
    private val testSuccessFilter = IFilter<Int, String> { it.toString() }
    private val testFailFilter = IFilter<Int, String> { null }


    @Test
    fun matchTest() {
        val result = testSuccessFilter.evaluate(1)

        assertEquals("1", result)
    }


    @Test
    fun conditionalTest() {
        assertNull(ConditionalFilter({ false }, null, testSuccessFilter).evaluate(0))
        assertNotNull(ConditionalFilter({ true }, null, testSuccessFilter).evaluate(0))
    }


    @Test
    fun conjunctionSuccessTest() {
        assertNotNull(ConjunctionFilter(testSuccessFilter).evaluate(0))
    }

    @Test
    fun conjunctionFailTest() {
        assertNull(ConjunctionFilter(testFailFilter).evaluate(0))
        assertNull(ConjunctionFilter(testSuccessFilter, testFailFilter).evaluate(0))
        assertNull(ConjunctionFilter(testFailFilter, testSuccessFilter).evaluate(0))
    }


    @Test
    fun firstMatchSuccessTest1() {
        assertNotNull(FirstMatchFilter(testSuccessFilter).evaluate(1))
        assertNotNull(FirstMatchFilter(testFailFilter, testSuccessFilter).evaluate(1))
        assertNotNull(FirstMatchFilter(testSuccessFilter, testFailFilter).evaluate(1))
    }

    @Test
    fun firstMatchSuccessTest2() {
        val matchFilter = IFilter<Int, String> { (-it).toString() }
        var res = FirstMatchFilter(testSuccessFilter, matchFilter).evaluate(1)

        assertEquals("1", res)

        res = FirstMatchFilter(matchFilter, testSuccessFilter).evaluate(1)

        assertEquals("-1", res)
    }

    @Test
    fun firstMatchFailTest() {
        assertNull(FirstMatchFilter(testFailFilter).evaluate(1))
    }


    @Test
    fun containingTest() {
        val filter = ContainingFilter((1..5).toSet())

        assertNull(filter.evaluate(0))
        assertNotNull(filter.evaluate(1))
    }


    @Test
    fun disjunctionSuccessTest() {
        assertNotNull(ConjunctionFilter(testSuccessFilter).evaluate(0))
    }

    @Test
    fun disjunctionFailTest() {
        assertNull(ConjunctionFilter(testFailFilter).evaluate(0))
        assertNull(ConjunctionFilter(testSuccessFilter, testFailFilter).evaluate(0))
        assertNull(ConjunctionFilter(testFailFilter, testSuccessFilter).evaluate(0))
    }


    @Test
    fun equalityTest() {
        val filter = EqualityFilter(1)

        assertNull(filter.evaluate(0))
        assertNotNull(filter.evaluate(1))
    }


    @Test
    fun negationSuccessTest() {
        val filter = NegationFilter(testFailFilter)

        assertNotNull(filter.evaluate(0))
    }

    @Test
    fun negationFailTest() {
        val filter = NegationFilter(testSuccessFilter)

        assertNull(filter.evaluate(0))
    }


    @Test
    fun nullGuardTest1() {
        val filter = InputNullGuardFilter(null, testSuccessFilter)

        assertNull(filter.evaluate(null))

        val result = filter.evaluate(1)
        assertEquals("1", result)
    }

    @Test
    fun nullGuardTest2() {
        val filter = InputNullGuardFilter(null, testFailFilter)

        assertNull(filter.evaluate(1))
    }


    @Test
    fun numberRangeTest1() {
        val filter = RangeFilter.atLeast(5)

        assertNotNull(filter.evaluate(10))
        assertNull(filter.evaluate(0))
    }

    @Test
    fun numberRangeTest2() {
        val filter = RangeFilter.atMost(5)

        assertNotNull(filter.evaluate(0))
        assertNull(filter.evaluate(10))
    }

    @Test
    fun numberRangeTest3() {
        val filter = RangeFilter.between(1, 10)

        assertNotNull(filter.evaluate(5))
        assertNull(filter.evaluate(0))
    }


    @Test
    fun optionalTest() {
        val filter = ConditionalFilter.optional(null, testSuccessFilter)

        assertNull(filter.evaluate(Optional.empty()))
        assertNotNull(filter.evaluate(Optional.of(1)))
    }


    @Test
    fun preprocessorTest() {
        val control = IFilter<String, String> { it }
        val filter = PreProcessorFilter(Int::toString, "Convert to string", control)

        assertEquals("1", filter.evaluate(1))
    }

    @Test
    fun nullGuardedPreprocessorTest() {
        val control = IFilter<String, String> { it }
        val filter = PreProcessorFilter.nullGuarded<Int?, String, String>(
            { it?.toString() },
            "Convert to string",
            null,
            control
        )

        assertNull(filter.evaluate(null))

        val result = filter.evaluate(1)
        assertEquals("1", result)
    }


    @Test
    fun postprocessorTest() {
        val control = IFilter<Int, Int> { it }
        val filter =
            PostProcessorFilter(control, "Convert result to string") { input, result ->
                input to result.toString()
            }

        val result = filter.evaluate(1)
        assertEquals(1 to "1", result)
    }


    @Test
    fun regularExpressionTest() {
        val filter = RegularExpressionFilter("^1$".toRegex())

        assertNotNull(filter.evaluate("1"))
        assertNull(filter.evaluate("11"))
    }
}
