import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day6 {

    @Test
    fun p1Samples() {
        assertEquals(5, doPart1("bvwbjplbgvbhsrlpgdmjqwftvncz"))
        assertEquals(6, doPart1("nppdvjthqldpwncqszvftbrmjlhg"))
        assertEquals(10, doPart1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
        assertEquals(11, doPart1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))
    }
    @Test
    fun part1() {
        val data = loadData().single()
        val answer = doPart1(data)

        assertEquals(1766, answer)
    }

    private fun doPart1(data: String): Int {
        return findWindow(data, 4);
    }

    @Test
    fun p2Samples() {
        assertEquals(19, doPart2("mjqjpqmgbljsphdztnvjfqwrcgsmlb"))
        assertEquals(23, doPart2("bvwbjplbgvbhsrlpgdmjqwftvncz"))
        assertEquals(23, doPart2("nppdvjthqldpwncqszvftbrmjlhg"))
        assertEquals(29, doPart2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
        assertEquals(26, doPart2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))
    }
    @Test
    fun part2() {
        val data = loadData().single()

        val answer = doPart2(data)

        assertEquals(2383, answer)
    }

    private fun doPart2(data: String): Int {
        return findWindow(data, 14)
    }

    private fun findWindow(data: String, windowSize: Int): Int {
        return data
            .windowed(windowSize)
            .indexOfFirst { w -> w.toSet().size == windowSize } + windowSize
    }

    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day6.txt").file).readLines()
    }
}