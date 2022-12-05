import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day5 {
    private val sample = """
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
        val (board, moves) = loadProblem(sample)

        moveSingle(board, moves)

        val answer = boardToAnswer(board)

        assertEquals("CMZ", answer)
    }

    @Test
    fun part1() {
        val (board, moves) = loadProblem()

        moveSingle(board, moves)

        val answer = boardToAnswer(board)

        assertEquals("BZLVHBWQF", answer)
    }

    @Test
    fun p2Test() {
        val (board, moves) = loadProblem(sample)

        moveMultiple(board, moves)

        val answer = boardToAnswer(board)

        assertEquals("MCD", answer)
    }

    @Test
    fun part2() {
        val (board, moves) = loadProblem()

        moveMultiple(board, moves)

        val answer = boardToAnswer(board)

        assertEquals("TDGJQTZSL", answer)
    }

    private fun boardToAnswer(board: List<MutableList<Char>>) =
        board.map { it.first() }.joinToString("")

    private fun loadProblem(data: List<String> = loadData()): Pair<List<MutableList<Char>>, List<List<Int>>> {
        val (boardLines, moveLines) = data.asSequence().delimited { a -> a.isBlank() }.toList()
        val board = loadBoard(boardLines)
        val moves = loadMoves(moveLines)
        return Pair(board, moves)
    }

    private fun moveSingle(
        board: List<MutableList<Char>>,
        moves: List<List<Int>>
    ) {
        moves.forEach { (num, from, to) ->
            (0 until num).forEach { _ ->
                board[to - 1].add(0, board[from - 1].removeFirst())
            }
        }
    }

    private fun moveMultiple(
        board: List<MutableList<Char>>,
        moves: List<List<Int>>
    ) {
        moves.forEach { (num, from, to) ->
            val block = board[from - 1].removeAt(0 until num)
            board[to - 1].addAll(0, block)
        }
    }

    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day5.txt").file).readLines()
    }

    private fun loadBoard(boardLines: List<String>): List<MutableList<Char>> {
        val (crateLines, indexLines) = boardLines.asSequence().split { !it.contains('[') }.toList()
        val crates = List(indexLines.single().allInts().size) { mutableListOf<Char>() }

        crateLines.forEach { l ->
            l.chunked(4).map { it[1] }.mapIndexed { i, c -> if (c != ' ') crates[i].add(c) }
        }

        return crates
    }

    private fun loadMoves(moveLines: List<String>): List<List<Int>> {
        return moveLines.map { it.allInts() }
    }

    fun <E> MutableList<E>.removeAt(r: IntRange): List<E> {

        val prefixRange = 0 until r.first
        val suffixRange = r.last + 1 until this.size

        val prefix = this.slice(prefixRange)
        val slice = this.slice(r)
        val suffix = this.slice(suffixRange)

        this.clear()
        this.addAll(prefix)
        this.addAll(suffix)

        return slice
    }
}
