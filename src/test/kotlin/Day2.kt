import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day2 {

    @Test
    fun part1() {
        val data = loadData()
        val answer = data
            .sumOf { scoreMap[it]!! }

        assertEquals(14264, answer)
    }

    @Test
    fun part2() {
        val data = loadData()

        val answer = data
            .map { Pair(it.first, resultMap[it]!!) }
            .sumOf { scoreMap[it]!! }

        assertEquals(12382, answer)
    }

    private val resultMap = mapOf(
        Pair(Pair('A', 'X'), 'Z'),
        Pair(Pair('A', 'Y'), 'X'),
        Pair(Pair('A', 'Z'), 'Y'),
        Pair(Pair('B', 'X'), 'X'),
        Pair(Pair('B', 'Y'), 'Y'),
        Pair(Pair('B', 'Z'), 'Z'),
        Pair(Pair('C', 'X'), 'Y'),
        Pair(Pair('C', 'Y'), 'Z'),
        Pair(Pair('C', 'Z'), 'X'),
    )

    private val scoreMap = mapOf(
        Pair(Pair('A', 'X'), 4),
        Pair(Pair('A', 'Y'), 8),
        Pair(Pair('A', 'Z'), 3),
        Pair(Pair('B', 'X'), 1),
        Pair(Pair('B', 'Y'), 5),
        Pair(Pair('B', 'Z'), 9),
        Pair(Pair('C', 'X'), 7),
        Pair(Pair('C', 'Y'), 2),
        Pair(Pair('C', 'Z'), 6),
    )
    private fun loadData(): List<Pair<Char, Char>> {
        return File(ClassLoader.getSystemResource("day2.txt").file).readLines()
            .map { Pair(it[0], it[2]) }
    }
}