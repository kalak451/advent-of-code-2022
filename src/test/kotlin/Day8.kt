import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class Day8 {

    @Test
    fun part1() {
        val data = loadData()

        val answer = runProcess(
            data,
            combine = { dirs -> if (dirs.any { it }) 1 else 0 },
            calc = { current, rng -> rng.all { it < current } }
        ).sum()

        assertEquals(1827, answer)
    }


    @Test
    fun part2() {
        val data = loadData()

        val answer = runProcess(
            data,
            combine = { it.reduce(Int::times) },
            calc = { current, rng ->
                val idx = rng.indexOfFirst { it >= current }
                if(idx == -1) {
                    rng.size
                } else {
                    idx + 1
                }
            }
        ).max()

        assertEquals(335580, answer)
    }

    private fun <T> runProcess(grid: List<List<Int>>, combine: (List<T>) -> Int, calc: (Int, List<Int>) -> T): List<Int> {
        return grid.indices.flatMap { row ->
            grid.indices.map { column ->
                val current = grid[row][column]
                val up = calc(current, grid.slice(row - 1 downTo 0).map { it[column] })
                val down = calc(current, grid.slice(row + 1 until grid.size).map { it[column] })
                val left = calc(current, grid[row].slice(column - 1 downTo 0))
                val right = calc(current, grid[row].slice(column + 1 until grid.size))
                combine(listOf(up, down, left, right))
            }
        }
    }

    private fun loadData(): List<List<Int>> {
        return File(ClassLoader.getSystemResource("day8.txt").file).readLines().map { it.map(Char::digitToInt) }
    }

}