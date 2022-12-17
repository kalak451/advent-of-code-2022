import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class Day17 {
    private val sampleWind = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"

    @Test
    fun part1Sample() {
        val wind = sampleWind.toList()
        val answer = play(2022, wind)

        assertEquals(3068, answer)
    }

    @Test
    fun part1() {
        val wind = loadData().single().toList()

        val answer = play(2022, wind)

        assertEquals(3219, answer)
    }

    @Test
    fun part2Sample() {
        val wind = sampleWind.toList()
        val answer = play(1_000_000_000_000, wind)

        assertEquals(1514285714288, answer)
    }

    @Test
    fun part2() {
        val wind = loadData().single().toList()

        val answer = play(1_000_000_000_000, wind)

        assertEquals(1582758620701, answer)
    }

    private fun play(totalShapes: Long, wind: List<Char>): Long {
        var repeatHeight = 0L
        var lookingForCycle = true
        val cycleCache: MutableMap<List<Any>, Pair<Long, Long>> = mutableMapOf()
        var shapeCount = 0L
        val board = Board(wind)

        while (shapeCount < totalShapes) {
            board.drop()

            if (lookingForCycle) {
                val newHeightIndex = board.maxRockIndex().toLong()
                val cacheKey = board.boardKey()
                if (cycleCache.containsKey(cacheKey)) {
                    //found a cycle!
                    lookingForCycle = false
                    val (oldShapeIndex, oldHeightIndex) = cycleCache[cacheKey]!!
                    val shapeDelta = shapeCount - oldShapeIndex
                    val heightDelta = newHeightIndex - oldHeightIndex
                    val repeats = (totalShapes - 1 - shapeCount) / shapeDelta
                    shapeCount += shapeDelta * repeats
                    repeatHeight = heightDelta * repeats
                } else {
                    cycleCache[cacheKey] = Pair(shapeCount, newHeightIndex)
                }
            }

            shapeCount++
        }

        return (board.maxRockIndex() + repeatHeight + 1)
    }

    class Board(private val wind: List<Char>) {
        private val shapes: List<List<String>> = listOf(
            listOf(
                "@@@@"
            ),
            listOf(
                ".@.",
                "@@@",
                ".@."
            ),
            listOf(
                "..@",
                "..@",
                "@@@",
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
        ).map { it.reversed() }


        private var shapeIndex: Int = 0
        private var windIndex: Int = 0

        private val grid = mutableListOf<MutableList<Char>>()

        private fun getShape(): List<String> {
            val shape = shapes[shapeIndex]
            shapeIndex++
            if (shapeIndex >= shapes.size) {
                shapeIndex = 0
            }

            return shape
        }

        private fun getWind(): Char {
            val w = wind[windIndex]
            windIndex++
            if (windIndex >= wind.size) {
                windIndex = 0
            }

            return w
        }

        fun print() {
            println(grid.reversed().joinToString("\n") { it.joinToString("") })
            println()
        }

        fun maxRockIndex(): Int {
            return grid.indexOfLast { it.contains('#') }
        }

        fun boardKey(): List<Any> {
            return listOf(shapeIndex, windIndex, maxIndices())
        }

        private fun prepGridTop(shape: List<String>) {
            val rockIndex = maxRockIndex()
            val sy = rockIndex + 3
            val maxIndexNeeded = sy + shape.size
            val linesToAdd = maxIndexNeeded - (grid.indices.lastOrNull() ?: -1)
            if (linesToAdd > 0) {
                grid.addAll(MutableList(linesToAdd) { MutableList(7) { '.' } })
            }
        }

        private fun checkCollision(spritePos: Pair<Int, Int>, shape: List<String>): Boolean {
            shape.indices.forEach { sy ->
                val by = sy + spritePos.second
                shape[sy].indices.forEach { sx ->
                    val bx = sx + spritePos.first
                    if (outOfBounds(Pair(bx, by))) {
                        return true
                    }

                    val spriteVal = shape[sy][sx]
                    val boardVal = grid[by][bx]

                    if (spriteVal == '@' && boardVal == '#') {
                        return true
                    }
                }
            }

            return false
        }

        private fun outOfBounds(p: Pair<Int, Int>): Boolean {
            if (p.first < 0) {
                return true
            }

            if (p.first >= grid.first().size) {
                return true
            }

            if (p.second < 0) {
                return true
            }

            if (p.second >= grid.size) {
                return true
            }

            return false
        }

        private fun applyShape(spritePos: Pair<Int, Int>, shape: List<String>) {
            shape.indices.forEach { sy ->
                val by = sy + spritePos.second
                shape[sy].indices.forEach { sx ->
                    val bx = sx + spritePos.first
                    if (shape[sy][sx] == '@') {
                        grid[by][bx] = '#'
                    }
                }
            }
        }

        private fun maxIndices(): List<Int?> {
            val base = maxRockIndex()
            return (0..6).map { col ->
                (base downTo 0).filter { grid[it][col] == '#' }.map { base - it }.firstOrNull() ?: -1
            }
        }

        fun drop() {
            val shape = getShape()

            prepGridTop(shape)

            var spritePos = Pair(2, maxRockIndex() + 4)
            assertFalse(checkCollision(spritePos, shape))

            var falling = true
            while (falling) {
                val windSpritePos = when (getWind()) {
                    '<' -> Pair(spritePos.first - 1, spritePos.second)
                    '>' -> Pair(spritePos.first + 1, spritePos.second)
                    else -> spritePos
                }

                if (!checkCollision(windSpritePos, shape)) {
                    spritePos = windSpritePos
                }

                val fallSpritePos = Pair(spritePos.first, spritePos.second - 1)
                if (!checkCollision(fallSpritePos, shape)) {
                    spritePos = fallSpritePos
                } else {
                    falling = false
                }
            }

            applyShape(spritePos, shape)
        }
    }

    private fun loadData(filename: String = "day17.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}