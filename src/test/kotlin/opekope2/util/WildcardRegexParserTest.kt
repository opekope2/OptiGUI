package opekope2.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WildcardRegexParserTest {
    @Test
    fun parseExactStringTest() {
        val filter = parseWildcardOrRegex("Exact String")

        assertFalse(filter.test("Exact String").skip)
        assertTrue(filter.test("Exact String").match)
        assertFalse(filter.test("Exact string").match)
        assertFalse(filter.test("exact string").match)
        assertFalse(filter.test("EXACT STRING").match)
        assertFalse(filter.test("Something Different").match)
    }

    @Test
    fun parsePatternTest() {
        val filter = parseWildcardOrRegex("pattern:?Letter to *")

        assertTrue(filter.test("Letter to Herobrine").match)
        assertTrue(filter.test("Letter to a creeper").match)
        assertTrue(filter.test("My Letter to John").match)
        assertFalse(filter.test("letter to Herobrine").match)
        assertFalse(filter.test("Letter from Herobrine").match)
    }

    @Test
    fun parseIPatternTest() {
        val filter = parseWildcardOrRegex("ipattern:Letter to *")

        assertTrue(filter.test("Letter to Herobrine").match)
        assertTrue(filter.test("Letter to a creeper").match)
        assertTrue(filter.test("letter to Herobrine").match)
        assertTrue(filter.test("letter to STEVE").match)
        assertFalse(filter.test("A letter to CJ").match)
        assertFalse(filter.test("Letter from Herobrine").match)
    }

    @Test
    fun parseRegexTest() {
        val filter = parseWildcardOrRegex("regex:Letter (to|from) .*")

        assertTrue(filter.test("Letter to Herobrine").match)
        assertTrue(filter.test("Letter from Herobrine").match)
        assertFalse(filter.test("letter to Herobrine").match)
        assertFalse(filter.test("A Letter to Herobrine").match)
    }

    @Test
    fun parseIRegexTest() {
        val filter = parseWildcardOrRegex("iregex:Letter (to|from) .*")

        assertTrue(filter.test("Letter to Herobrine").match)
        assertTrue(filter.test("Letter from Herobrine").match)
        assertTrue(filter.test("letter to Herobrine").match)
        assertTrue(filter.test("LETTER TO HEROBRINE").match)
        assertFalse(filter.test("A Letter to Herobrine").match)
        assertFalse(filter.test("LETTER TOFROM HEROBRINE").match)
    }
}
