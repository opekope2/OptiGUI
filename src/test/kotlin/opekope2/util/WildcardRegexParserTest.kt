package opekope2.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WildcardRegexParserTest {
    @Test
    fun parseExactStringTest() {
        val filter = parseWildcardOrRegex("Exact String")

        assertTrue(filter.matches("Exact String"))
        assertFalse(filter.matches("Exact string"))
        assertFalse(filter.matches("exact string"))
        assertFalse(filter.matches("EXACT STRING"))
        assertFalse(filter.matches("Something Different"))
    }

    @Test
    fun parsePatternTest() {
        val filter = parseWildcardOrRegex("pattern:?Letter to *")

        assertTrue(filter.matches("Letter to Herobrine"))
        assertTrue(filter.matches("Letter to a creeper"))
        assertTrue(filter.matches("My Letter to John"))
        assertFalse(filter.matches("letter to Herobrine"))
        assertFalse(filter.matches("Letter from Herobrine"))
    }

    @Test
    fun parseIPatternTest() {
        val filter = parseWildcardOrRegex("ipattern:Letter to *")

        assertTrue(filter.matches("Letter to Herobrine"))
        assertTrue(filter.matches("Letter to a creeper"))
        assertTrue(filter.matches("letter to Herobrine"))
        assertTrue(filter.matches("letter to STEVE"))
        assertFalse(filter.matches("A letter to CJ"))
        assertFalse(filter.matches("Letter from Herobrine"))
    }

    @Test
    fun parseRegexTest() {
        val filter = parseWildcardOrRegex("regex:Letter (to|from) .*")

        assertTrue(filter.matches("Letter to Herobrine"))
        assertTrue(filter.matches("Letter from Herobrine"))
        assertFalse(filter.matches("letter to Herobrine"))
        assertFalse(filter.matches("A Letter to Herobrine"))
    }

    @Test
    fun parseIRegexTest() {
        val filter = parseWildcardOrRegex("iregex:Letter (to|from) .*")

        assertTrue(filter.matches("Letter to Herobrine"))
        assertTrue(filter.matches("Letter from Herobrine"))
        assertTrue(filter.matches("letter to Herobrine"))
        assertTrue(filter.matches("LETTER TO HEROBRINE"))
        assertFalse(filter.matches("A Letter to Herobrine"))
        assertFalse(filter.matches("LETTER TOFROM HEROBRINE"))
    }
}
