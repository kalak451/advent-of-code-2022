import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

class Day14 {

    @Test
    fun part1Sample() {
        val data = """
            498,4 -> 498,6 -> 496,6
            503,4 -> 502,4 -> 502,9 -> 494,9
        """.trimIndent().split("\n")
        val lines = parse(data)

        val (board, sandPos) = buildBoard(lines)

        val answer = simulate(sandPos, board)

        println()
        println()
        board.print()

        assertEquals(24, answer)
    }

    @Test
    fun part1() {
        val data = loadData()
        val lines = parse(data)

        val (board, sandPos) = buildBoard(lines)

        val answer = simulate(sandPos, board)

        println()
        println()
        board.print()

        assertEquals(715, answer)
    }

    @Test
    fun part2Sample() {
        val data = """
            498,4 -> 498,6 -> 496,6
            503,4 -> 502,4 -> 502,9 -> 494,9
        """.trimIndent().split("\n")
        val lines = parse(data)

        val (board, sandPos) = buildBoard(lines, includeFloor = true)

        val answer = simulate(sandPos, board)

        println()
        println()
        board.print()

        assertEquals(93, answer)
    }

    @Test
    fun part2() {
        val data = loadData()
        val lines = parse(data)

        val (board, sandPos) = buildBoard(lines, includeFloor = true)

        val answer = simulate(sandPos, board)

        println()
        println()
        board.print()

        assertEquals(25248, answer)
    }

    private fun simulate(sandPos: Pair<Int, Int>, board: MutableList<MutableList<Char>>): Int {
        var cnt = 0
        var s = dropSand(sandPos, board)

        while (s != null) {
            cnt++
            board.set(s, 'o')
//            println(cnt + 1)
//            board.print()
//            println()
//            println()

            if(s == sandPos) {
                return cnt
            }

            s = dropSand(sandPos, board)

        }

        return cnt
    }

    private fun dropSand(sandPos: Pair<Int, Int>, board: MutableList<MutableList<Char>>): Pair<Int, Int>? {
        var pnt = fallNext(sandPos, board)
        var last: Pair<Int, Int>? = null

        while (pnt != last) {
            if (pnt == null) {
                return null
            }
            last = pnt
            pnt = fallNext(pnt, board)
        }

        return last
    }

    private fun fallNext(p: Pair<Int, Int>, board: MutableList<MutableList<Char>>): Pair<Int, Int>? {
        val down = Pair(p.first, p.second + 1)
        val downLeft = Pair(p.first - 1, p.second + 1)
        val downRight = Pair(p.first + 1, p.second + 1)

        if (!board.isInGrid(down)) {
            return null
        }

        if (board.isValidForSand(down)) {
            return down
        }

        if (!board.isInGrid(downLeft)) {
            return null
        }

        if (board.isValidForSand(downLeft)) {
            return downLeft
        }

        if (!board.isInGrid(downRight)) {
            return null
        }

        if (board.isValidForSand(downRight)) {
            return downRight
        }

        return p;
    }

    private fun buildBoard(
        lines: List<List<Pair<Int, Int>>>,
        includeFloor: Boolean = false
    ): Pair<MutableList<MutableList<Char>>, Pair<Int, Int>> {
        val (_, offset) = calculateXDims(lines, includeFloor)
        val offsetLines = offsetLines(offset, lines)

        val board = buildBaseBoard(offsetLines, includeFloor)
        val sandPos = Pair(500 - offset, 0)
        board.set(sandPos, '+')

        offsetLines.flatMap { line ->
            line.windowed(2).flatMap { (a, b) -> a.pointsBetween(b) }
        }.forEach { p -> board.set(p, '#') }

        return Pair(board, sandPos)
    }

    private fun buildBaseBoard(
        offsetLines: List<List<Pair<Int, Int>>>,
        includeFloor: Boolean
    ): MutableList<MutableList<Char>> {
        val (offsetMaxX, _) = calculateXDims(offsetLines, includeFloor)
        val yValues = offsetLines.flatten().map { it.second }
        val maxY = yValues.max()
        val floorOffset = if (includeFloor) 2 else 0
        val board = MutableList(maxY + floorOffset + 1) { MutableList(offsetMaxX + 1) { '.' } }
        if (includeFloor) {
            board[board.size - 1] = MutableList(offsetMaxX + 1) { '#' }
        }

        return board
    }

    private fun offsetLines(offset: Int, lines: List<List<Pair<Int, Int>>>): List<List<Pair<Int, Int>>> {

        return lines.map { line -> line.map { Pair(it.first - offset, it.second) } }
    }

    private fun calculateXDims(lines: List<List<Pair<Int, Int>>>, includeFloor: Boolean): Pair<Int, Int> {
        val xValues = lines.flatten().map { it.first }
        val maxX = xValues.max()
        val minX = xValues.min()

        if (includeFloor) {
            return Pair(maxX + 200, minX - 200)
        } else {
            return Pair(maxX, minX)
        }
    }

    fun Pair<Int, Int>.pointsBetween(b: Pair<Int, Int>): List<Pair<Int, Int>> {
        return if (this.first == b.first) {
            //y moves
            (min(this.second, b.second)..max(this.second, b.second)).map { y -> Pair(this.first, y) }
        } else {
            //x moves
            (min(this.first, b.first)..max(this.first, b.first)).map { x -> Pair(x, this.second) }
        }
    }

    fun MutableList<MutableList<Char>>.print() {
        this.forEach { line -> println(line.joinToString("")) }
    }

    fun MutableList<MutableList<Char>>.isInGrid(p: Pair<Int, Int>): Boolean {
        if (p.first < 0) {
            return false
        }

        if (p.first >= this.first().size) {
            return false
        }

        if (p.second < 0) {
            return false
        }

        if (p.second >= this.size) {
            return false
        }

        return true
    }

    fun MutableList<MutableList<Char>>.isValidForSand(p: Pair<Int, Int>): Boolean {
        if (!this.isInGrid(p)) {
            return false
        }

        if (this.at(p) == '.') {
            return true
        }

        if (this.at(p) == '+') {
            return true
        }

        return false
    }

    fun MutableList<MutableList<Char>>.set(p: Pair<Int, Int>, v: Char) {
        this[p.second][p.first] = v
    }

    fun MutableList<MutableList<Char>>.at(p: Pair<Int, Int>): Char {
        return this[p.second][p.first]
    }

    private fun parse(line: String): List<Pair<Int, Int>> {
        return line
            .split(" -> ")
            .map { it.allInts() }
            .map { (x, y) -> Pair(x, y) }
    }

    private fun parse(lines: List<String>): List<List<Pair<Int, Int>>> {
        return lines.map { parse(it) }
    }

    private fun loadData(filename: String = "day14.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}