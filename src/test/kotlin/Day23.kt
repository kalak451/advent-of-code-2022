import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day23 {

    private val searchDirections = listOf(
        listOf(Pair(0, -1), Pair(-1, -1), Pair(1,-1)),
        listOf(Pair(0, 1), Pair(-1, 1), Pair(1, 1)),
        listOf(Pair(-1, 0), Pair(-1,-1), Pair(-1, 1)),
        listOf(Pair(1, 0), Pair(1,-1), Pair(1, 1)),
    )

    private val allDirections = searchDirections.flatten().distinct()

    private val smallSample = """
        .....
        ..##.
        ..#..
        .....
        ..##.
        .....
    """.trimIndent().split("\n")

    private val sample = """
        ....#..
        ..###.#
        #...#.#
        .#...##
        #.###..
        ##.#.##
        .#..#..
    """.trimIndent().split("\n")

    @Test
    fun part1SmallSample() {
        val emptyGround = runPart1(smallSample, 3).second

        assertEquals(25, emptyGround)
    }

    @Test
    fun part1Sample() {
        val emptyGround = runPart1(sample, 10).second

        assertEquals(110, emptyGround)
    }

    @Test
    fun part1() {
        val emptyGround = runPart1(loadData(), 10).second

        assertEquals(4082, emptyGround)
    }

    @Test
    fun part2Sample() {
        val rounds = runPart1(sample, 5000).first

        assertEquals(20, rounds)
    }

    @Test
    fun part2() {
        val rounds = runPart1(loadData(), 5000).first

        assertEquals(1065, rounds)
    }

    private fun runPart1(input: List<String>, rounds: Int): Pair<Int, Int> {
        val startingPoints = toPoints(input)

        var roundsRun = 0
        var searchIndices = (0..3).toList()
        var points = startingPoints

        while(roundsRun < rounds) {
            val proposals = proposeMoves(points, searchIndices)
            val newPoints = proposals.flatMap { (k, v) ->
                if (v.size == 1) {
                    listOf(k)
                } else {
                    v
                }
            }.toSet()
            assertEquals(startingPoints.size, newPoints.size)


            searchIndices = searchIndices.map { (it + 1) % searchDirections.size }
            roundsRun++

            if(points == newPoints) {
                break
            }
            points = newPoints
        }

        val minX = points.minOf { it.first }
        val maxX = points.maxOf { it.first }

        val minY = points.minOf { it.second }
        val maxY = points.maxOf { it.second }

        val coords = (minX..maxX).flatMap { x ->
            (minY..maxY).map { y ->
                Pair(x, y)
            }
        }

        val emptyGround = coords.count { !points.contains(it) }
        return Pair(roundsRun, emptyGround)
    }

    private fun proposeMoves(input: Set<Pair<Int,Int>>, searchIndices: List<Int>): Map<Pair<Int,Int>, List<Pair<Int, Int>>> {
        val results = mutableMapOf<Pair<Int,Int>, List<Pair<Int, Int>>>()

        input.forEach { p ->
            if(allDirections.map { p.move(it) }.none { input.contains(it) }) {
                results.compute(p) { pp, acc ->
                    if(acc == null) {
                        listOf(pp)
                    } else {
                        acc + listOf(pp)
                    }
                }
            } else {
                val idxToUse = searchIndices
                    .firstOrNull() { idx ->
                        searchDirections[idx].map { p.move(it) }.none { input.contains(it) }
                    }

                if(idxToUse == null) {
                    results.compute(p) { pp, acc ->
                        if(acc == null) {
                            listOf(pp)
                        } else {
                            acc + listOf(pp)
                        }
                    }
                } else {
                    val proposedDirection = searchDirections[idxToUse].first()
                    val proposedMove = p.move(proposedDirection)
                    results.compute(proposedMove) { _, acc ->
                        if (acc == null) {
                            listOf(p)
                        } else {
                            acc + listOf(p)
                        }
                    }
                }
            }
        }

        return results
    }

    private fun toPoints(input: List<String>): Set<Pair<Int, Int>> {
        return input.flatMapIndexed { y: Int, s: String ->
            s.mapIndexedNotNull { x, c ->
                if(c == '#') {
                    Pair(x,y)
                } else {
                    null
                }
            }
        }.toSet()
    }

    private fun Pair<Int,Int>.move(d: Pair<Int,Int>): Pair<Int,Int> {
        return Pair(
            this.first + d.first,
            this.second + d.second
        )
    }

    private fun loadData(filename: String = "day23.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}