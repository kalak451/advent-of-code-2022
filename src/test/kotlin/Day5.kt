import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class Day5 {
    val sample = """
    [D]
[N] [C]
[Z] [M] [P]
 1   2   3

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
        """.trimIndent().split("\n");
    @Test
    fun p1Test() {
        val data = sample

        val board = loadBoard(data)
        val moves = loadMoves(data)

        moveSingle(board, moves)

        val answer = board.map { it.peek() }.joinToString("")

        assertEquals("CMZ", answer)
    }
    @Test
    fun part1() {
        val data = loadData();

        val board = loadBoard(data)
        val moves = loadMoves(data)

        moveSingle(board, moves)

        val answer = board.map { it.peek() }.joinToString("")

        assertEquals("BZLVHBWQF", answer)
    }

    @Test
    fun p2Test() {
        val data = sample

        val board = loadBoard(data)
        val moves = loadMoves(data)

        moveMultiple(board, moves)

        val answer = board.map { it.peek() }.joinToString("")

        assertEquals("MCD", answer)
    }

    @Test
    fun part2() {
        val data = loadData();
        val board = loadBoard(data)
        val moves = loadMoves(data)

        moveMultiple(board, moves)

        val answer = board.map { it.peek() }.joinToString("")

        assertEquals("TDGJQTZSL", answer)
    }

    private fun moveSingle(
        board: List<Stack<Char>>,
        moves: List<Triple<Int, Int, Int>>
    ) {
        moves.forEach { mv ->
            (0 until mv.first).forEach { _ ->
                val x = board[mv.second - 1].pop()
                board[mv.third - 1].push(x)
            }
        }
    }

    private fun moveMultiple(
        board: List<Stack<Char>>,
        moves: List<Triple<Int, Int, Int>>
    ) {
        moves.forEach { mv ->
            val tempStack = Stack<Char>()
            (0 until mv.first).forEach { _ ->
                tempStack.push(board[mv.second - 1].pop())
            }

            (0 until mv.first).forEach { _ ->
                board[mv.third - 1].push(tempStack.pop())
            }
        }
    }

    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day5.txt").file).readLines()
    }

    private fun loadBoard(data: List<String>): List<Stack<Char>> {
        val boardLines = data
            .takeWhile { it.isNotBlank() }

        val numCols = boardLines.last().split(' ').last().toInt()
        val board = List(numCols) { Stack<Char>() }

        boardLines.takeWhile { it.contains('[') }.forEach { line ->
            line.chunked(4).forEachIndexed { idx, chunk ->
                val letter = chunk[1]
                if (!letter.isWhitespace()) {
                    board[idx].push(letter)
                }
            }
        }

        return board.map { s->
            val x = Stack<Char>()
            s.toList().reversed().forEach { x.push(it) }
            x
        }
    }

    private fun loadMoves(data: List<String>): List<Triple<Int, Int, Int>> {
        val moveLines = data.dropWhile { !it.startsWith("move") }

        val pattern = Regex("^move (\\d*) from (\\d*) to (\\d*)$")

        return moveLines
            .map { pattern.matchEntire(it)!! }
            .map { Triple(it.groupValues[1].toInt(), it.groupValues[2].toInt(), it.groupValues[3].toInt()) }
    }

    fun allTheInts(input: String): List<Int> {
        val pattern = Regex("-?\\d+")

        return pattern
            .findAll(input)
            .map { it.value.toInt() }
            .toList()
    }
}
