import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day1 {
    @Test
    fun part1() {
        val maxCal = processData()
            .max()

        assertEquals(71471, maxCal)
    }

    @Test
    fun part2() {
        val answer = processData()
            .sortedDescending()
            .take(3)
            .sum()

        assertEquals(211189, answer)
    }

    private fun processData(): Sequence<Long> {
        val data = loadData()

        return data
            .asSequence()
            .delimited { x -> x.isBlank() }
            .map { x -> x.sumOf { it.toLong() } }
    }

    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day1.txt").file).readLines()
    }
}