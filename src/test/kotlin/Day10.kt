import java.io.File
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class Day10 {

    @Test
    fun part1() {
        val instructions = loadData("day10.txt").toMutableList()

        val interestingCycles = setOf(20, 60, 100, 140, 180, 220)

        val results = runMachine(instructions, 220)

        val interestingValues = results
            .withIndex()
            .filter { v -> interestingCycles.contains(v.index + 1) }
            .toList()

        assertEquals(16480, interestingValues.map { it.value * (it.index + 1) }.sum())
    }

    @Test
    fun part2() {
        val instructions = loadData("day10.txt").toMutableList()

        val results = runMachine(instructions, 240).chunked(40)

        val img = results
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

        img.forEach { println(it) }

        val expected = """
            ###..#....####.####.#..#.#....###..###..
            #..#.#....#....#....#..#.#....#..#.#..#.
            #..#.#....###..###..#..#.#....#..#.###..
            ###..#....#....#....#..#.#....###..#..#.
            #....#....#....#....#..#.#....#....#..#.
            #....####.####.#.....##..####.#....###..
        """.trimIndent()

        assertEquals(expected, img.joinToString("\n"))
    }

    @Test
    fun part2Sample1() {
        val instructions = loadData("day10-sample1.txt").toMutableList()

        val results = runMachine(instructions, 240).chunked(40)

        val img = results
            .map { it.withIndex() }
            .map { it.map { v ->
                val dist = abs(v.index - v.value)
                if(dist <= 1) {
                    '#'
                } else {
                    '.'
                }
            } }
            .map { it.joinToString("") }

        img.forEach { println(it) }

        val expected = """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent()

        assertEquals(expected, img.joinToString("\n"))

    }

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

        val interestingCycles = setOf(20, 60, 100, 140, 180, 220)

        val results = runMachine(instructions, 220)

        assertEquals(220, results.size)

        val interestingValues = results
            .withIndex()
            .filter { v -> interestingCycles.contains(v.index + 1) }
            .toList()

        assertEquals(listOf(21L, 19, 18, 21, 16, 18), interestingValues.map { it.value }.toList())

        assertEquals(13140, interestingValues.map { it.value * (it.index + 1) }.sum())
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

    private fun loadData(filename: String): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}