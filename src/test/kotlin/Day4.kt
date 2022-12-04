import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day4 {

    @Test
    fun part1() {
        val data = loadData();
        val answer = data
            .map { processData(it) }
            .filter { fullyContainsEither(it.first, it.second) }
            .count()

        assertEquals(599, answer)
    }

    @Test
    fun part2() {
        val data = loadData()

        val answer = data
            .map { processData(it) }
            .filter { overlapsAtAll(it.first, it.second) }
            .count()

        assertEquals(928, answer)
    }

    private fun fullyContainsEither(a: IntRange, b: IntRange): Boolean {
        val aSet = a.toSet()
        val bSet = b.toSet()
        return aSet.containsAll(bSet) || bSet.containsAll(aSet)
    }

    private fun overlapsAtAll(a: IntRange, b: IntRange): Boolean {
        return a.intersect(b).isNotEmpty()
    }

    private fun processData(input: String): Pair<IntRange,IntRange> {
        val ranges = input.split(',');
        assertEquals(2, ranges.size)

        return Pair(genRange(ranges[0]), genRange(ranges[1]))
    }

    private fun genRange(input: String): IntRange {
        val values = input.split('-')
        assertEquals(2, values.size)

        return (values[0].toInt()..values[1].toInt())
    }
    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day4.txt").file).readLines()
    }
}