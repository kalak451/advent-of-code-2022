import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Day24 {

    private val openSample = """
        #.#####
        #.....#
        #.....#
        #.....#
        #.....#
        #.....#
        #####.#        
    """.trimIndent().split("\n")

    private val smallSample = """
        #.#####
        #.....#
        #>....#
        #.....#
        #...v.#
        #.....#
        #####.#        
    """.trimIndent().split("\n")

    private val sample = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent().split("\n")

    @Test
    fun stormMoveTest() {
        var storms = getStorms(smallSample)
        assertTrue(storms.isStormAt(Pair(1, 2)))
        assertTrue(storms.isStormAt(Pair(4, 4)))

        storms = storms.tick()
        assertTrue(storms.isStormAt(Pair(2, 2)))
        assertTrue(storms.isStormAt(Pair(4, 5)))

        storms = storms.tick()
        assertTrue(storms.isStormAt(Pair(3, 2)))
        assertTrue(storms.isStormAt(Pair(4, 1)))

        storms = storms.tick()
        assertTrue(storms.isStormAt(Pair(4, 2)))
        assertTrue(storms.isStormAt(Pair(4, 2)))

        storms = storms.tick()
        assertTrue(storms.isStormAt(Pair(5, 2)))
        assertTrue(storms.isStormAt(Pair(4, 3)))

        storms = storms.tick()
        assertTrue(storms.isStormAt(Pair(1, 2)))
        assertTrue(storms.isStormAt(Pair(4, 4)))
    }

    @Test
    fun testAllAdj() {
        val storms = getStorms(openSample)
        val dists = shortestPath(Pair(1, 0)) { p ->
            val moves = findMoves(p, storms)
            moves.map { Pair(it, 1) }
        }

        assertEquals(27, dists.size)
    }

    @Test
    fun testAdjEnd() {
        val storms = getStorms(smallSample)

        val results = findMoves(Pair(5, 5), storms).toSet()

        assertEquals(4, results.size)
        assertTrue(results.contains(Pair(5, 5)))
        assertTrue(results.contains(Pair(5, 6)))
        assertTrue(results.contains(Pair(5, 4)))
        assertTrue(results.contains(Pair(4, 5)))
        assertFalse(results.contains(Pair(6, 5)))
    }

    @Test
    fun testAdjStart() {
        val storms = getStorms(smallSample)

        val results = findMoves(Pair(1, 1), storms).toSet()

        assertEquals(3, results.size)
        assertTrue(results.contains(Pair(1, 1)))
        assertTrue(results.contains(Pair(1, 0)))
        assertFalse(results.contains(Pair(1, 2)))
        assertTrue(results.contains(Pair(2, 1)))
        assertFalse(results.contains(Pair(0, 1)))
    }

    @Test
    fun pathTest() {
        val storms = getStorms(sample)
        val start = Pair(1, 0)
        val end = Pair(storms.maxX, storms.maxY + 1)

        val allStorms = (0..25).fold(mutableListOf(storms)) { acc, _ ->
            acc.add(acc.last().tick())
            acc
        }

        val correctPath = listOf(
            Pair(1, 0),
            Pair(1, 1),
            Pair(1, 2),
            Pair(1, 2),
            Pair(1, 1),
            Pair(2, 1),
            Pair(3, 1),
            Pair(3, 2),
            Pair(2, 2),
            Pair(2, 1),
            Pair(3, 1),
            Pair(3, 1),
            Pair(3, 2),
            Pair(3, 3),
            Pair(4, 3),
            Pair(5, 3),
            Pair(6, 3),
            Pair(6, 4),
            Pair(6, 5),
        )

        val generatedPath = listOf(
            Pair(1, 0),
            Pair(1, 0),
            Pair(1, 0),
            Pair(1, 0),
            Pair(1, 0),
            Pair(1, 0),
            Pair(1, 0),
            Pair(1, 0),
            Pair(1, 1),
            Pair(2, 1),
            Pair(3, 1),
            Pair(3, 1),
            Pair(3, 2),
            Pair(3, 3),
            Pair(4, 3),
            Pair(5, 3),
            Pair(6, 3),
            Pair(6, 4),
            Pair(6, 5),
        )

        val pathAndStorm = generatedPath.zip(allStorms)

        pathAndStorm
            .windowed(2)
            .forEach { (a, b) ->
                val (aP, aS) = a
                val (bP, bS) = b

                val moves = findMoves(aP, bS)

                assertTrue(moves.contains(bP))
            }

//        pathAndStorm.forEach { printBoard(buildBoard(it.second, it.first)) }
    }

    fun printBoard(board: List<List<Char>>) {
        println()
        println(board.map { it.joinToString("") }.joinToString("\n"))
    }

    private fun toBoard(input: String): List<List<Char>> {
        return input.split("\n").map { it.toList() }
    }

    private fun buildBoard(
        storms: StormSet,
        p: Pair<Int, Int>?
    ): List<List<Char>> {
        val board = MutableList(storms.maxY + 1) { y ->
            MutableList(storms.maxX + 1) { x -> if (x == 0 || y == 0 || x == storms.maxX || y == storms.maxY) '#' else '.' }
        }

        board[0][1] = '.'
        board[storms.maxY][storms.maxX - 1] = '.'

        storms.storms.groupBy { it.p }
            .forEach { g ->
                if (g.value.size == 1) {
                    board[g.key.second][g.key.first] = g.value.first().dir
                } else {
                    board[g.key.second][g.key.first] = g.value.size.toString().first()
                }
            }

        if (p != null) {
            if (board[p.second][p.first] == '.') {
                board[p.second][p.first] = 'E'
            } else {
                throw RuntimeException("Currently Placed at invalid point!")
            }
        }
        return board
    }

    @Test
    fun part1Sample() {
        val storms = getStorms(sample)
        assertEquals(19, storms.storms.size)

        val start = Pair(1, 0)
        val end = Pair(storms.maxX - 1, storms.maxY)

        assertEquals(Pair(6, 5), end)

        val allStorms = (0..25).fold(mutableListOf(storms)) { acc, _ ->
            acc.add(acc.last().tick())
            acc
        }

        val path = p1Work(0, start, allStorms, end)!!

        assertEquals(start, path.first())
        assertEquals(end, path.last())
        assertEquals(19, path.size)

    }

    @Test
    fun part1() {
        val storms = getStorms(loadData())

        val start = Pair(1, 0)
        val end = Pair(storms.maxX - 1, storms.maxY)

        val allStorms = (0..1000).fold(mutableListOf(storms)) { acc, _ ->
            acc.add(acc.last().tick())
            acc
        }

        val path = p1Work(0, start, allStorms, end)!!

        assertEquals(start, path.first())
        assertEquals(end, path.last())
        assertEquals(231, path.size)
    }

    @Test
    fun part2Sample() {
        val storms = getStorms(sample)

        val start = Pair(1, 0)
        val end = Pair(storms.maxX - 1, storms.maxY)

        val allStorms = (0..100).fold(mutableListOf(storms)) { acc, _ ->
            acc.add(acc.last().tick())
            acc
        }

        val pathA = p1Work(0, start, allStorms, end)!!
        val pathB = p1Work(pathA.size - 1, end, allStorms, start)!!
        val pathC = p1Work(pathA.size - 1 + pathB.size - 1, start, allStorms, end)!!

        assertEquals(19, pathA.size)
        assertEquals(24, pathB.size)
        assertEquals(14, pathC.size)

        assertEquals(54, pathA.size - 1 + pathB.size - 1 + pathC.size - 1)
    }

    @Test
    fun part2() {
        val storms = getStorms(loadData())

        val start = Pair(1, 0)
        val end = Pair(storms.maxX - 1, storms.maxY)

        val allStorms = (0..1000).fold(mutableListOf(storms)) { acc, _ ->
            acc.add(acc.last().tick())
            acc
        }

        val pathA = p1Work(0, start, allStorms, end)!!
        val pathB = p1Work(pathA.size - 1, end, allStorms, start)!!
        val pathC = p1Work(pathA.size - 1 + pathB.size - 1, start, allStorms, end)!!

//        assertEquals(19, pathA.size)
//        assertEquals(24, pathB.size)
//        assertEquals(14, pathC.size)

        assertEquals(713, pathA.size - 1 + pathB.size - 1 + pathC.size - 1)
    }

    fun p1Work(
        tick: Int,
        p: Pair<Int, Int>,
        allStorms: List<StormSet>,
        end: Pair<Int, Int>,
        cache: MutableMap<List<Any>, List<Pair<Int, Int>>?> = mutableMapOf()
    ): List<Pair<Int, Int>>? {

        val cacheKey = listOf(tick, p)

        if (cache.containsKey(cacheKey)) {
            return cache[cacheKey]
        }

        if (p == end) {
            cache[cacheKey] = listOf(p)
            return cache[cacheKey]
        }

        if (tick + 1 >= allStorms.size) {
            cache[cacheKey] = null
            return null
        }

        val nextStorms = allStorms[tick + 1]
        val nextMoves = findMoves(p, nextStorms)

        val result = nextMoves
            .mapNotNull { p1Work(tick + 1, it, allStorms, end, cache) }
            .map { listOf(p) + it }
            .minByOrNull { it.size }
        cache[cacheKey] = result
        return cache[cacheKey]
    }

    private fun findMoves(p: Pair<Int, Int>, storms: StormSet): List<Pair<Int, Int>> {
        val possibleMoves = sequenceOf(
            p,
            Pair(p.first - 1, p.second),
            Pair(p.first + 1, p.second),
            Pair(p.first, p.second - 1),
            Pair(p.first, p.second + 1),
        )

        return possibleMoves
            .filter { it.first >= 1 && it.second >= 0 }
            .filter { it.first <= storms.maxX - 1 && it.second <= storms.maxY }
            .filter { it.second != 0 || it.first == 1 }
            .filter { it.second != storms.maxY || it.first == storms.maxX - 1 }
            .filter { !storms.isStormAt(it) }
            .toList()
    }

    private fun getStorms(input: List<String>): StormSet {
        val storms = input.flatMapIndexed { y, l ->
            l.trim().mapIndexedNotNull { x, c ->
                if (c == '#' || c == '.') {
                    null
                } else {
                    Storm(Pair(x, y), c)
                }
            }
        }

        return StormSet(storms, input.first().length - 1, input.size - 1)
    }

    data class StormSet(val storms: List<Storm>, val maxX: Int, val maxY: Int) {
        private val stormIndex: Set<Pair<Int, Int>> = storms.map { it.p }.toSet()

        fun isStormAt(q: Pair<Int, Int>): Boolean {
            return stormIndex.contains(q)
        }

        fun tick(): StormSet {
            val newStorms = storms.map { s ->
                val newP = when (s.dir) {
                    '<' -> Pair(s.p.first - 1, s.p.second)
                    '>' -> Pair(s.p.first + 1, s.p.second)
                    '^' -> Pair(s.p.first, s.p.second - 1)
                    'v' -> Pair(s.p.first, s.p.second + 1)
                    else -> throw RuntimeException("Invalid direction ${s.dir}")
                }

                val wrappedP = if (newP.first == 0) {
                    Pair(maxX - 1, newP.second)
                } else if (newP.first == maxX) {
                    Pair(1, newP.second)
                } else if (newP.second == 0) {
                    Pair(newP.first, maxY - 1)
                } else if (newP.second == maxY) {
                    Pair(newP.first, 1)
                } else {
                    newP
                }

                Storm(wrappedP, s.dir)
            }

            return StormSet(newStorms, maxX, maxY)
        }
    }

    data class Storm(val p: Pair<Int, Int>, val dir: Char)

    private fun loadData(filename: String = "day24.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}