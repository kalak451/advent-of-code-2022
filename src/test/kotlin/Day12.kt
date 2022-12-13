import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals


class Day12 {
    val p1Sample = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent()

    fun parseInput(input: List<String>): Triple<Pair<Int, Int>, Pair<Int, Int>, List<List<Int>>> {
        var start = Pair(-1,-1)
        var end = Pair(-1,-1)

        val heightMap = input.mapIndexed { rI, r ->
            r.mapIndexed { cI, v ->
                when (v) {
                    'S' -> {
                        start = Pair(cI, rI)
                        0
                    }
                    'E' -> {
                        end = Pair(cI, rI)
                        25
                    }
                    else -> {
                        v.code - 'a'.code
                    }
                }
            }
        }

        return Triple(start, end, heightMap)
    }

    fun adjacentMovesP1(p: Pair<Int, Int>, heightMap: List<List<Int>>): List<Pair<Int, Int>> {
        val (x,y) = p
        val currentElevation = heightMap[y][x]

        val pointsToMove = mutableListOf<Pair<Int,Int>>()
        if(x - 1 >=0) {
            val p = Pair(x - 1, y)
            val newElevation = heightMap[p.second][p.first]
            if(newElevation <= currentElevation + 1) {
                pointsToMove.add(p)
            }
        }

        if(x + 1 < heightMap.first().size) {
            val p = Pair(x + 1, y)
            val newElevation = heightMap[p.second][p.first]
            if(newElevation <= currentElevation + 1) {
                pointsToMove.add(p)
            }
        }

        if(y - 1 >= 0) {
            val p = Pair(x, y - 1)
            val newElevation = heightMap[p.second][p.first]
            if(newElevation <= currentElevation + 1) {
                pointsToMove.add(p)
            }
        }

        if(y + 1 < heightMap.size) {
            val p = Pair(x, y + 1)
            val newElevation = heightMap[p.second][p.first]
            if(newElevation <= currentElevation + 1) {
                pointsToMove.add(p)
            }
        }

        return pointsToMove
    }

    fun adjacentMovesP2(p: Pair<Int, Int>, heightMap: List<List<Int>>): List<Pair<Int, Int>> {
        val (x,y) = p
        val currentElevation = heightMap[y][x]

        val pointsToMove = mutableListOf<Pair<Int,Int>>()
        if(x - 1 >=0) {
            val p = Pair(x - 1, y)
            val newElevation = heightMap[p.second][p.first]
            if(newElevation >= currentElevation - 1) {
                pointsToMove.add(p)
            }
        }

        if(x + 1 < heightMap.first().size) {
            val p = Pair(x + 1, y)
            val newElevation = heightMap[p.second][p.first]
            if(newElevation >= currentElevation - 1) {
                pointsToMove.add(p)
            }
        }

        if(y - 1 >= 0) {
            val p = Pair(x, y - 1)
            val newElevation = heightMap[p.second][p.first]
            if(newElevation >= currentElevation - 1) {
                pointsToMove.add(p)
            }
        }

        if(y + 1 < heightMap.size) {
            val p = Pair(x, y + 1)
            val newElevation = heightMap[p.second][p.first]
            if(newElevation >= currentElevation - 1) {
                pointsToMove.add(p)
            }
        }

        return pointsToMove
    }

    @Test
    fun p1Sample() {
        val (start, end, heightMap) = parseInput(p1Sample.split("\n"))

        val map = shortestPath(start) {p -> adjacentMovesP1(p, heightMap).map { Pair(it, 1) } }

        val answer = map[end]!!

        assertEquals(31, answer)
    }

    @Test
    fun p1() {
        val (start, end, heightMap) = parseInput(loadData("day12.txt"))

        val map = shortestPath(start) {p -> adjacentMovesP1(p, heightMap).map { Pair(it, 1) } }

        val answer = map[end]!!

        assertEquals(0, answer)
    }

    @Test
    fun p2Sample() {
        val (_, start, heightMap) = parseInput(p1Sample.split("\n"))

        val map = shortestPath(start) {p -> adjacentMovesP2(p, heightMap).map { Pair(it, 1) } }

        val answer = heightMap.indices.flatMap {r -> heightMap.first().indices.map { c -> Pair(c,r)  } }
            .filter { p -> heightMap[p.second][p.first] == 0 }
            .map { p -> map[p]!! }
            .min()

        assertEquals(29, answer)

    }

    @Test
    fun part2() {
        val (_, start, heightMap) = parseInput(loadData("day12.txt"))

        val map = shortestPath(start) {p -> adjacentMovesP2(p, heightMap).map { Pair(it, 1) } }

        val answer = heightMap.indices.flatMap {r -> heightMap.first().indices.map { c -> Pair(c,r)  } }
            .filter { p -> heightMap[p.second][p.first] == 0 }
            .filter { p -> map.containsKey(p) }
            .map { p -> map[p]!! }
            .min()

        assertEquals(388, answer)

    }

    private fun loadData(filename: String): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}