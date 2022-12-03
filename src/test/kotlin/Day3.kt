import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day3 {
    @Test
    fun part1() {
        val data = loadData()
        val answer = data.map { findCommon(it) }.sumOf { getPriority(it) }

        assertEquals(7428, answer)
    }

    @Test
    fun part2() {
        val data = loadData()
        val answer = data.chunked(3).map { findCommon(it) }.sumOf { getPriority(it) }

        assertEquals(2650, answer)
    }

    private fun findCommon(input: String): Char {
        val l = input.length / 2
        val p1 = input.subSequence(0, l)
        val p2 = input.subSequence(l, input.length)

        val intersect = p1.toSet().intersect(p2.toSet())

        assertEquals(1, intersect.size)

        return intersect.first()
    }

    private fun findCommon(input: List<String>): Char {
        val p1 = input[0]
        val p2 = input[1]
        val p3 = input[2]

        val intersect = p1.toSet().intersect(p2.toSet()).intersect(p3.toSet())

        assertEquals(1, intersect.size)

        return intersect.first()
    }

    fun getPriority(c: Char): Int {
        return if (c.isLowerCase()) {
            c.code - 97 + 1
        } else if (c.isUpperCase()) {
            c.code - 65 + 27
        } else {
            -1
        }
    }

    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day3.txt").file).readLines()
    }

    @Test
    fun shouldGetPriorities() {
        assertEquals(1, getPriority('a'))
        assertEquals(27, getPriority('A'))
        assertEquals(16, getPriority('p'))
        assertEquals(38, getPriority('L'))
        assertEquals(42, getPriority('P'))
        assertEquals(22, getPriority('v'))
        assertEquals(20, getPriority('t'))
        assertEquals(19, getPriority('s'))
    }
}