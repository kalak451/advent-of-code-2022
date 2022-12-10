import java.io.File
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class Day10 {
    private val interestingCycles = setOf(20, 60, 100, 140, 180, 220)

    @Test
    fun part1Sample() {
        val instructions = mutableListOf(
            "noop",
            "addx 3",
            "addx -5"
        )

        val results = runMachine(instructions, 6)

        assertEquals(listOf(1L, 1L, 1L, 4L, 4L, -1L), results)
    }

    @Test
    fun part1Sample1() {
        val instructions = loadData("day10-sample1.txt").toMutableList()

        val results = runMachine(instructions, 220)

        assertEquals(220, results.size)

        val interestingValues = extractInterestingValues(results)

        assertEquals(listOf(21L, 19, 18, 21, 16, 18), interestingValues.map { it.value }.toList())

        assertEquals(13140, interestingValues.map { it.value * (it.index + 1) }.sum())
    }

    @Test
    fun part1() {
        val instructions = loadData("day10.txt").toMutableList()

        val results = runMachine(instructions, 220)

        val interestingValues = extractInterestingValues(results)

        assertEquals(16480, interestingValues.map { it.value * (it.index + 1) }.sum())
    }

    @Test
    fun part2Sample1() {
        val instructions = loadData("day10-sample1.txt").toMutableList()

        val results = runMachine(instructions, 240)

        val img = generateImage(results)

        println(img)

        val expected = """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent()

        assertEquals(expected, img)

    }

    @Test
    fun part2() {
        val instructions = loadData("day10.txt").toMutableList()

        val results = runMachine(instructions, 240)

        val img = generateImage(results)

        println(img)

        val expected = """
            ###..#....####.####.#..#.#....###..###..
            #..#.#....#....#....#..#.#....#..#.#..#.
            #..#.#....###..###..#..#.#....#..#.###..
            ###..#....#....#....#..#.#....###..#..#.
            #....#....#....#....#..#.#....#....#..#.
            #....####.####.#.....##..####.#....###..
        """.trimIndent()

        assertEquals(expected, img)
    }

    private fun runMachine(instructions: MutableList<String>, toRun: Int): List<Long> {
        var x = 1L
        var instructionCycle = 0
        var valToAdd = 0

        return (1..toRun).map { cycle ->
            if (instructionCycle == 0) {
                x += valToAdd

                val inst = if(instructions.isEmpty()) {
                    "noop"
                } else {
                    instructions.removeFirst()
                }

                if (inst.startsWith("noop")) {
                    valToAdd = 0
                    instructionCycle = 1
                } else if (inst.startsWith("addx")) {
                    valToAdd = inst.allInts().single()
                    instructionCycle = 2
                }
            }

            instructionCycle--
            x
        }
    }

    private fun generateImage(results: List<Long>): String {
        val img = results
            .asSequence()
            .chunked(40)
            .map { it.withIndex() }
            .map {
                it.map { v ->
                    val dist = abs(v.index - v.value)
                    if (dist <= 1) {
                        '#'
                    } else {
                        '.'
                    }
                }
            }
            .map { it.joinToString("") }
            .joinToString("\n")
        return img
    }

    private fun extractInterestingValues(results: List<Long>): List<IndexedValue<Long>> {
        return results
            .withIndex()
            .filter { v -> interestingCycles.contains(v.index + 1) }
            .toList()
    }

    private fun loadData(filename: String): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}