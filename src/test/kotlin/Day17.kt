import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class Day17 {
    val shapes: List<List<String>> = listOf(
        listOf(
            "@@@@"
        ),
        listOf(
            ".@.",
            "@@@",
            ".@."
        ),
        listOf(
            "@@@",
            "..@",
            "..@",
        ),
        listOf(
            "@",
            "@",
            "@",
            "@"
        ),
        listOf(
            "@@",
            "@@"
        )
    )

    @Test
    fun part1Sample() {
        val wind = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>".toList()
        val answer = playPart1(2022, wind)

        assertEquals(3068, answer)
    }

    @Test
    fun part1() {
        val wind = loadData().single().toList()

        val answer = playPart1(2022, wind)

        assertEquals(3219, answer)
    }

    @Test
    fun part2Sample() {
        val wind = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>".toList()
        val answer = playPart1(1_000_000_000_000, wind)

        assertEquals(1514285714288, answer)
    }

    @Test
    fun part2() {
        val wind = loadData().single().toList()

        val answer = playPart1(1_000_000_000_000, wind)

        assertEquals(1582758620701, answer)
    }

    private fun playPart1(shapeCount: Long, wind: List<Char>): Long {
        var repeatHeight = 0L
        var cacheHit = false
        val cache: MutableMap<List<Any>, Pair<Long, Long>> = mutableMapOf()
        var windIndex = 0
        var shapeIndex = 0L
        val board = mutableListOf<MutableList<Char>>()
        while (shapeIndex < shapeCount) {
            val s = shapes[(shapeIndex % shapes.size).toInt()]

            val rockHeight = maxRockHeight(board)
            val sy = rockHeight + 4
            val maxHeightNeeded = sy + s.size
            val linesToAdd = maxHeightNeeded - board.size
            if (linesToAdd > 0) {
                board.addAll(MutableList(linesToAdd) { MutableList(7) { '.' } })
            }

            var spritePos = Pair(2, rockHeight + 4)
            assertFalse(checkCollision(spritePos, s, board))

            var landed = false
            while (!landed) {
                val windSpritePos = when (wind[windIndex]) {
                    '<' -> Pair(spritePos.first - 1, spritePos.second)
                    '>' -> Pair(spritePos.first + 1, spritePos.second)
                    else -> spritePos
                }

                if (!checkCollision(windSpritePos, s, board)) {
                    spritePos = windSpritePos
                }

                val fallSpritePos = Pair(spritePos.first, spritePos.second - 1)
                if (!checkCollision(fallSpritePos, s, board)) {
                    spritePos = fallSpritePos
                } else {
                    landed = true
                }
                windIndex++
                if (windIndex >= wind.size) {
                    windIndex = 0
                }
            }

            applyShape(spritePos, s, board)

            if (!cacheHit) {
                val newHeight = maxRockHeight(board).toLong()
                val cacheKey = listOf(shapeIndex % shapes.size, windIndex, maxHeights(board))
                if (cache.containsKey(cacheKey)) {
                    //found a cycle!
                    cacheHit = true
                    val (oldIdx, oldHeight) = cache[cacheKey]!!
                    val shapeDelta = shapeIndex - oldIdx
                    val heightDelta = newHeight - oldHeight
                    val repeats = (shapeCount - 1 - shapeIndex) / shapeDelta
                    shapeIndex += shapeDelta * repeats
                    repeatHeight = heightDelta * repeats
                } else {
                    cache[cacheKey] = Pair(shapeIndex, newHeight)
                }
            }

            shapeIndex++
        }
        return (maxRockHeight(board) + repeatHeight + 1)
    }

    private fun applyShape(spritePos: Pair<Int, Int>, shape: List<String>, board: MutableList<MutableList<Char>>) {
        shape.indices.forEach { sy ->
            val by = sy + spritePos.second
            shape[sy].indices.forEach { sx ->
                val bx = sx + spritePos.first
                if (shape[sy][sx] == '@') {
                    board[by][bx] = '#'
                }
            }
        }
    }

    private fun checkCollision(spritePos: Pair<Int, Int>, shape: List<String>, board: List<List<Char>>): Boolean {
        shape.indices.forEach { sy ->
            val by = sy + spritePos.second
            shape[sy].indices.forEach { sx ->
                val bx = sx + spritePos.first
                if (board.outOfBounds(Pair(bx, by))) {
                    return true
                }

                val spriteVal = shape[sy][sx]
                val boardVal = board[by][bx]

                if (spriteVal == '@' && boardVal == '#') {
                    return true
                }
            }
        }

        return false
    }

    private fun maxRockHeight(board: List<List<Char>>): Int {
        return board.indexOfLast { it.contains('#') }
    }

    private fun maxHeights(board: List<List<Char>>): List<Int?> {
        val base = maxRockHeight(board)
        return (0..6).map { col ->
            (base downTo 0).filter { board[it][col] == '#' }.map { base - it }.firstOrNull()
        }
    }

    private fun List<List<Char>>.outOfBounds(p: Pair<Int, Int>): Boolean {
        if (p.first < 0) {
            return true
        }

        if (p.first >= this.first().size) {
            return true
        }

        if (p.second < 0) {
            return true
        }

        if (p.second >= this.size) {
            return true
        }

        return false
    }

    private fun printBoard(board: List<List<Char>>) {
        println()
        println(board.reversed().asSequence().map { it.joinToString("") }.joinToString("\n"))
        println()
    }

    private fun loadData(filename: String = "day17.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}