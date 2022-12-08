import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class Day8 {
    @Test
    fun part1() {
        val data = loadData()

        val visible = calcVisible(data)

        val answer = visible.size
        assertEquals(1827, answer)
    }

    @Test
    fun part2Sample() {
        val data = listOf(
            "30373",
            "25512",
            "65332",
            "33549",
            "35390",
        )

        assertEquals(4, calculateScore(data, 2, 1))
        assertEquals(8, calculateScore(data, 2, 3))

        val answer = calculateScore(data)

        assertEquals(8, answer)
    }

    @Test
    fun part2() {
        val data = loadData()

        val answer = calculateScore(data)

        assertEquals(335580, answer)
    }

    private fun calculateScore(data: List<String>): Int {
        val answer = data.first().indices.flatMap { x -> data.indices.map { y -> Pair(x, y) } }
            .map { (x, y) -> calculateScore(data, x, y) }
            .max()
        return answer
    }

    private fun calcVisible(data: List<String>): MutableList<Pair<Int, Int>> {
        val xSize = data.first().length
        val ySize = data.size

        val visible = mutableListOf<Pair<Int, Int>>()

        for (x in 0 until xSize) {
            visible.add(Pair(x, 0))
            visible.add(Pair(x, ySize - 1))
        }

        for (y in 1 until ySize - 1) {
            visible.add(Pair(0, y))
            visible.add(Pair(xSize - 1, y))
        }

        for (x in 1 until xSize - 1) {
            for (y in 1 until ySize - 1) {
                if (isVisible(data, x, y)) {
                    visible.add(Pair(x, y))
                }
            }
        }
        return visible
    }

    private fun calculateScore(grid: List<String>, x: Int, y: Int): Int {
        val left = checkLeft(grid, x, y)
        val correctedLeft = if (left == -1) x else left

        val right = checkRight(grid, x, y)
        val correctedRight = if (right == -1) (grid.first().length - 1 - x) else right

        val up = checkUp(grid, x, y)
        val correctedUp = if (up == -1) y else up

        val down = checkDown(grid, x, y)
        val correctedDown = if(down == -1) (grid.size - 1 -y) else down

        return correctedLeft * correctedRight * correctedUp * correctedDown
    }

    private fun isVisible(grid: List<String>, x: Int, y: Int) =
        (checkLeft(grid, x, y) == -1
                || checkRight(grid, x, y) == -1
                || checkUp(grid, x, y) == -1
                || checkDown(grid, x, y) == -1)

    private fun checkLeft(
        grid: List<String>,
        x: Int,
        y: Int
    ): Int {
        val currentHeight = grid[y][x].digitToInt()
        val idx = (x - 1 downTo 0)
            .map { grid[y][it].digitToInt() }
            .indexOfFirst { z -> z >= currentHeight }

        return if (idx == -1) {
            -1
        } else {
            idx + 1
        }
    }

    private fun checkUp(
        grid: List<String>,
        x: Int,
        y: Int
    ): Int {
        val currentHeight = grid[y][x].digitToInt()
        val idx = (y - 1 downTo 0)
            .map { grid[it][x].digitToInt() }
            .indexOfFirst { z -> z >= currentHeight }

        return if (idx == -1) {
            -1
        } else {
            idx + 1
        }
    }

    private fun checkRight(
        grid: List<String>,
        x: Int,
        y: Int
    ): Int {
        val currentHeight = grid[y][x].digitToInt()
        val idx = (x+1 until grid.first().length)
            .map { grid[y][it].digitToInt() }
            .indexOfFirst { z -> z >= currentHeight }

        return if (idx == -1) {
            -1
        } else {
            idx + 1
        }
    }

    private fun checkDown(
        grid: List<String>,
        x: Int,
        y: Int
    ): Int {
        val currentHeight = grid[y][x].digitToInt()
        val idx = (y+1 until grid.size)
            .map { grid[it][x].digitToInt() }
            .indexOfFirst { z -> z >= currentHeight }

        return if (idx == -1) {
            -1
        } else {
            idx + 1
        }
    }

    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day8.txt").file).readLines()
    }
}