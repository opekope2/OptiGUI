package opekope2.util

import net.minecraft.util.Identifier
import kotlin.test.*

class VillagerProfessionParserKtTest {
    @Test
    fun test1() {
        val v = tryParseProfession("cleric")

        assertNotNull(v)
        assertEquals(Identifier.DEFAULT_NAMESPACE, v.first.namespace)
        assertEquals("cleric", v.first.path)
        assertTrue(v.second.isEmpty())
    }

    @Test
    fun test2() {
        val v = tryParseProfession("optigui:worker")

        assertNotNull(v)
        assertEquals("optigui", v.first.namespace)
        assertEquals("worker", v.first.path)
        assertTrue(v.second.isEmpty())
    }

    @Test
    fun test3() {
        val v = tryParseProfession("cleric:1")

        assertNotNull(v)
        assertEquals(Identifier.DEFAULT_NAMESPACE, v.first.namespace)
        assertEquals("cleric", v.first.path)
        assertEquals(1, v.second.size)

        val level = v.second.first()
        assertTrue(level.isNumber)
        assertEquals(1, level.value)
    }

    @Test
    fun test4() {
        val v = tryParseProfession("cleric:1,3-4")

        assertNotNull(v)
        assertEquals(Identifier.DEFAULT_NAMESPACE, v.first.namespace)
        assertEquals("cleric", v.first.path)
        assertEquals(2, v.second.size)

        var level = v.second.first()
        assertTrue(level.isNumber)
        assertEquals(1, level.value)

        level = v.second.toList()[1]
        assertTrue(level.isRange)
        assertEquals(3, level.start)
        assertEquals(4, level.end)
    }

    @Test
    fun test5() {
        val v = tryParseProfession("optigui:tester:1,3-4")

        assertNotNull(v)
        assertEquals("optigui", v.first.namespace)
        assertEquals("tester", v.first.path)
        assertEquals(2, v.second.size)

        var level = v.second.first()
        assertTrue(level.isNumber)
        assertEquals(1, level.value)

        level = v.second.toList()[1]
        assertTrue(level.isRange)
        assertEquals(3, level.start)
        assertEquals(4, level.end)
    }

    @Test
    fun test6() {
        val v = tryParseProfession("optigui:overflow:test:1")
        assertNull(v)
    }
}
