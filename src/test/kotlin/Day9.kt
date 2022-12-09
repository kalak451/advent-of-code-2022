import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.abs
import kotlin.math.sign
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day9 {

    @Test
    fun part1Sample() {
        val data = """
            R 4
            U 4
            L 3
            D 1
            R 4
            D 1
            L 5
            R 2            
        """.trimIndent().split("\n").map { it.trim() }

        val tailPos = doMoves(data, 2)

        assertEquals(13, tailPos.size)
    }

    @Test
    fun part1() {
        val data = loadData()

        val tailPos = doMoves(data, 2)

        assertEquals(6057, tailPos.size)
    }

    @Test
    fun part2Sample() {
        val data = """
            R 4
            U 4
            L 3
            D 1
            R 4
            D 1
            L 5
            R 2            
        """.trimIndent().split("\n").map { it.trim() }

        val tailPos = doMoves(data, 10)

        assertEquals(1, tailPos.size)
    }

    @Test
    fun part2Sample2() {
        val data = """
            R 5
            U 8
            L 8
            D 3
            R 17
            D 10
            L 25
            U 20       
        """.trimIndent().split("\n").map { it.trim() }

        val tailPos = doMoves(data, 10)

        assertEquals(36, tailPos.size)
    }

    @Test
    fun part2() {
        val data = loadData()

        val tailPos = doMoves(data, 10)

        assertEquals(2514, tailPos.size)
    }

    private fun doMoves(data: List<String>, nodeCount: Int): MutableSet<Pair<Int, Int>> {
        val nodes = MutableList(nodeCount) { Pair(0, 0) }

        val tailPos = mutableSetOf<Pair<Int, Int>>()
        tailPos.add(nodes.last())

        data.forEach { line ->
            val d = line[0]
            val cnt = line.allInts().single()

            (0 until cnt).forEach { idx ->
                nodes[0] = moveHead(nodes[0], d)
                (1 until nodes.size).forEach { i ->
                    nodes[i] = moveTail(nodes[i - 1], nodes[i])
                }
                tailPos.add(nodes.last())
            }
        }
        return tailPos
    }

    private fun moveHead(p: Pair<Int, Int>, d: Char): Pair<Int, Int> {
        return when (d) {
            'U' -> Pair(p.first, p.second + 1)
            'D' -> Pair(p.first, p.second - 1)
            'L' -> Pair(p.first - 1, p.second)
            'R' -> Pair(p.first + 1, p.second)
            else -> throw Exception("Bad move!!")
        }
    }

    @Test
    fun tailMoveTests() {
        assertEquals(Pair(2, 1), moveTail(Pair(3, 1), Pair(1, 1)))
        assertEquals(Pair(1, 2), moveTail(Pair(1, 3), Pair(1, 1)))
        assertEquals(Pair(2, 2), moveTail(Pair(2, 1), Pair(1, 3)))
        assertEquals(Pair(2, 2), moveTail(Pair(3, 2), Pair(1, 3)))

        assertEquals(Pair(1, 1), moveTail(Pair(2, 1), Pair(1, 1)))
        assertEquals(Pair(2, 2), moveTail(Pair(1, 1), Pair(2, 2)))
        assertEquals(Pair(2, 2), moveTail(Pair(2, 2), Pair(2, 2)))

        assertEquals(Pair(1, 1), moveTail(Pair(2, 2), Pair(0, 0)))
    }

    private fun moveTail(h: Pair<Int, Int>, t: Pair<Int, Int>): Pair<Int, Int> {
        val hDist = h.first - t.first
        val vDist = h.second - t.second
        if (abs(vDist) >= 3 || abs(hDist) >= 3) {
            assertTrue(false, "Unexpected Move!")
        }

        return if(abs(vDist) >= 2 || abs(hDist) >= 2) {
            Pair(
                t.first + hDist.sign,
                t.second + vDist.sign
            )
        } else {
            t
        }
    }

    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day9.txt").file).readLines()
    }
}