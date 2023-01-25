package opekope2.util

import kotlin.test.*
import opekope2.util.NumberOrRange.Companion.parse

class NumberOrRangeTest {
    @Test
    fun parseMalformedTest() {
        assertNull(parse("malformed"))
        assertNull(parse("-"))
        assertNull(parse("(1-5)"))
        assertNull(parse("(1)"))
        assertNull(parse("(-1)"))
        assertNull(parse("(1-)"))
        assertNull(parse("(-)"))
    }

    @Test
    fun parseNumberTest1() {
        val v = parse("1")

        assertNotNull(v)

        assertTrue(v.isNumber)
        assertFalse(v.isRange)

        assertNull(v.start)
        assertNull(v.end)
        assertEquals(1, v.value)
    }

    @Test
    fun parseNumberTest2() {
        val v = parse("-1")

        assertNotNull(v)

        assertTrue(v.isNumber)
        assertFalse(v.isRange)

        assertNull(v.start)
        assertNull(v.end)
        assertEquals(-1, v.value)
    }

    @Test
    fun parseRangeTest1() {
        val v = parse("1-5")

        assertNotNull(v)

        assertFalse(v.isNumber)
        assertTrue(v.isRange)

        assertEquals(1, v.start)
        assertEquals(5, v.end)
        assertNull(v.value)
    }

    @Test
    fun parseRangeTest2() {
        val v = parse("(1)-5")

        assertNotNull(v)

        assertFalse(v.isNumber)
        assertTrue(v.isRange)

        assertEquals(1, v.start)
        assertEquals(5, v.end)
        assertNull(v.value)
    }

    @Test
    fun parseRangeTest3() {
        val v = parse("1-(5)")

        assertNotNull(v)

        assertFalse(v.isNumber)
        assertTrue(v.isRange)

        assertEquals(1, v.start)
        assertEquals(5, v.end)
        assertNull(v.value)
    }

    @Test
    fun parseRangeTest4() {
        val v = parse("(1)-(5)")

        assertNotNull(v)

        assertFalse(v.isNumber)
        assertTrue(v.isRange)

        assertEquals(1, v.start)
        assertEquals(5, v.end)
        assertNull(v.value)
    }

    @Test
    fun parseRangeTest5() {
        val v = parse("(-1)-5")

        assertNotNull(v)

        assertFalse(v.isNumber)
        assertTrue(v.isRange)

        assertEquals(-1, v.start)
        assertEquals(5, v.end)
        assertNull(v.value)
    }

    @Test
    fun parseRangeTest6() {
        val v = parse("1-(-5)")

        assertNotNull(v)

        assertFalse(v.isNumber)
        assertTrue(v.isRange)

        assertEquals(1, v.start)
        assertEquals(-5, v.end)
        assertNull(v.value)
    }

    @Test
    fun parseRangeTest7() {
        val v = parse("(-1)-(-5)")

        assertNotNull(v)

        assertFalse(v.isNumber)
        assertTrue(v.isRange)

        assertEquals(-1, v.start)
        assertEquals(-5, v.end)
        assertNull(v.value)
    }
}
