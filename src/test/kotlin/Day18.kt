import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class Day18 {

    @Test
    fun part1() {
        val data = loadData()
        val coords = data.map { it.allInts() }.toSet()

        val answer = coords.sumOf { (x, y, z) ->
            listOf(
                listOf(x - 1, y, z),
                listOf(x + 1, y, z),
                listOf(x, y - 1, z),
                listOf(x, y + 1, z),
                listOf(x, y, z - 1),
                listOf(x, y, z + 1)
            ).count { !coords.contains(it) }
        }

        assertEquals(4390, answer)
    }

    @Test
    fun part2() {
        val data = loadData()
        val coords = data.map { it.allInts() }.toSet()

        val answer = coords.sumOf { (x, y, z) ->
            listOf(
                listOf(x - 1, y, z),
                listOf(x + 1, y, z),
                listOf(x, y - 1, z),
                listOf(x, y + 1, z),
                listOf(x, y, z - 1),
                listOf(x, y, z + 1)
            ).count { checkPart2(it, coords) }
        }

        assertEquals(2534, answer)
    }

    private fun checkPart2(
        c: List<Int>,
        coords: Set<List<Int>>
    ): Boolean {
        if (coords.contains(c)) {
            return false
        }


        return !buildShape(coords, c, mutableSetOf())
    }

    private fun buildShape(coords: Set<List<Int>>, c: List<Int>, amassed: MutableSet<List<Int>>): Boolean {
        if (amassed.contains(c)) {
            return true
        }

        if (coords.contains(c)) {
            return true
        }

        if (c.any { it > 21 }) {
            return false
        }

        if(c.any {it < 0}) {
            return false
        }

        amassed.add(c)

        val (x, y, z) = c

        return listOf(
            listOf(x - 1, y, z),
            listOf(x + 1, y, z),
            listOf(x, y - 1, z),
            listOf(x, y + 1, z),
            listOf(x, y, z - 1),
            listOf(x, y, z + 1)
        ).all {
            buildShape(coords, it, amassed)
        }
    }

    private fun loadData(filename: String = "day18.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}