import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class Day22 {
    val sample = """
                ...#
                .#..
                #...
                ....
        ...#.......#
        ........#...
        ..#....#....
        ..........#.
                ...#....
                .....#..
                .#......
                ......#.
        
        10R5L5R10L4R5L5
    """.trimIndent().split("\n")

    private val wrapsSample = listOf(
        Pair(Up, 0..3) to Pair(Down, 11 downTo 8),
        Pair(Up, 4..7) to Pair(Right, 0..3),
        Pair(Up, 8..11) to Pair(Down, 3 downTo 0),
        Pair(Up, 12..15) to Pair(Left, 7 downTo 4),
        Pair(Down, 0..3) to Pair(Up, 11 downTo 8),
        Pair(Down, 4..7) to Pair(Right, 11 downTo 8),
        Pair(Down, 8..11) to Pair(Up, 3 downTo 0),
        Pair(Down, 12..15) to Pair(Right, 7 downTo 4),
        Pair(Left, 0..3) to Pair(Down, 4..7),
        Pair(Left, 4..7) to Pair(Up, 15 downTo 12),
        Pair(Left, 8..11) to Pair(Up, 7 downTo 4),
        Pair(Right, 0..3) to Pair(Left, 11 downTo 8),
        Pair(Right, 4..7) to Pair(Down, 15 downTo 12),
        Pair(Right, 8..11) to Pair(Left, 3 downTo 0),
    )

    private val wraps = listOf(
        Pair(Up, 0..49) to Pair(Right, 50..99),
        Pair(Up, 50..99) to Pair(Right, 150..199),
        Pair(Up, 100..149) to Pair(Up, 0..49),
        Pair(Down, 0..49) to Pair(Down, 100..149),
        Pair(Down, 50..99) to Pair(Left, 150..199),
        Pair(Down, 100..149) to Pair(Left, 50..99),
        Pair(Left, 0..49) to Pair(Right, 149 downTo 100),
        Pair(Left, 50..99) to Pair(Down, 0..49),
        Pair(Left, 100..149) to Pair(Right, 49 downTo 0),
        Pair(Left, 150..199) to Pair(Down, 50..99),
        Pair(Right, 0..49) to Pair(Left, 149 downTo 100),
        Pair(Right, 50..99) to Pair(Up, 100..149),
        Pair(Right, 100..149) to Pair(Left, 49 downTo 0),
        Pair(Right, 150..199) to Pair(Up, 50..99),
    )

    @Test
    fun part1Sample() {
        val (board, actions) = parseInput(sample)
        val start = determineStart(board)
        val dir = Right

        assertEquals(Pair(8, 0), start)

        val (currentLoc, currentDir) = runBoard(start, dir, actions, board)

        board.print()

        val score = (1000 * (currentLoc.second + 1)) + (4 * (currentLoc.first + 1)) + currentDir.score

        assertEquals(6032, score)
    }

    @Test
    fun part1() {
        val (board, actions) = parseInput(loadData())
        val start = determineStart(board)
        val dir = Right

        assertEquals(Pair(50, 0), start)

        val (currentLoc, currentDir) = runBoard(start, dir, actions, board)

        board.print()

        val score = (1000 * (currentLoc.second + 1)) + (4 * (currentLoc.first + 1)) + currentDir.score

        assertEquals(146092, score)
    }

    @Test
    fun part2Sample() {
        val (board, actions) = parseInput(sample)
        val start = determineStart(board)
        val dir = Right

        assertEquals(Pair(8, 0), start)

        val (currentLoc, currentDir) = runBoard(start, dir, actions, board, wrapsSample)

        board.print()

        val score = (1000 * (currentLoc.second + 1)) + (4 * (currentLoc.first + 1)) + currentDir.score

        assertEquals(5031, score)
    }

    @Test
    fun part2() {
        val (board, actions) = parseInput(loadData())
        val start = determineStart(board)
        val dir = Right

        assertEquals(Pair(50, 0), start)

        val (currentLoc, currentDir) = runBoard(start, dir, actions, board, wraps)

        board.print()

        val score = (1000 * (currentLoc.second + 1)) + (4 * (currentLoc.first + 1)) + currentDir.score

        assertEquals(110342, score)
    }

    private fun runBoard(
        startLoc: Pair<Int, Int>,
        startDir: Right,
        actions: List<Action>,
        board: MutableList<MutableList<Char>>
    ): Pair<Pair<Int, Int>, Direction> {
        var currentLoc = startLoc
        var currentDir: Direction = startDir
        actions.forEach { action ->
            when (action) {
                is Turn -> currentDir = nextDirection(currentDir, action)
                is Move -> {
                    repeat(action.cnt) {
                        board[currentLoc.second][currentLoc.first] = currentDir.sym
                        currentLoc = determineNext(currentLoc, currentDir, board)
                    }
                }
            }
        }
        return Pair(currentLoc, currentDir)
    }

    private fun runBoard(
        startLoc: Pair<Int, Int>,
        startDir: Right,
        actions: List<Action>,
        board: MutableList<MutableList<Char>>,
        wrapsDef: List<Pair<Pair<Direction, IntProgression>, Pair<Direction, IntProgression>>>
    ): Pair<Pair<Int, Int>, Direction> {
        var currentLoc = startLoc
        var currentDir: Direction = startDir
        actions.forEach { action ->
            when (action) {
                is Turn -> currentDir = nextDirection(currentDir, action)
                is Move -> {
                    repeat(action.cnt) {
                        board[currentLoc.second][currentLoc.first] = currentDir.sym
                        val (nd, nl) = determineNext(currentLoc, currentDir, board, wrapsDef)
                        currentDir = nd
                        currentLoc = nl
                    }
                }
            }
        }
        return Pair(currentLoc, currentDir)
    }

    private fun determineNext(current: Pair<Int, Int>, dir: Direction, board: List<List<Char>>): Pair<Int, Int> {
        val initialNext = current.move(dir)

        val wrappedNext = if (board.outOfBounds(initialNext)) {
            when (dir) {
                Up -> {
                    val newY = board.map { it[initialNext.first] }.indexOfLast { it != ' ' }
                    Pair(initialNext.first, newY)
                }

                Down -> {
                    val newY = board.map { it[initialNext.first] }.indexOfFirst { it != ' ' }
                    Pair(initialNext.first, newY)
                }

                Left -> {
                    val newX = board[initialNext.second].indexOfLast { it != ' ' }
                    Pair(newX, initialNext.second)
                }

                Right -> {
                    val newX = board[initialNext.second].indexOfFirst { it != ' ' }
                    Pair(newX, initialNext.second)
                }
            }
        } else {
            initialNext
        }

        return if (board[wrappedNext.second][wrappedNext.first] == '#') {
            current
        } else {
            wrappedNext
        }
    }

    private fun determineNext(
        current: Pair<Int, Int>,
        dir: Direction,
        board: List<List<Char>>,
        wrapsDef: List<Pair<Pair<Direction, IntProgression>, Pair<Direction, IntProgression>>>
    ): Pair<Direction, Pair<Int, Int>> {
        val initialNext = current.move(dir)

        val (wrapDir, wrapNext) = if (board.outOfBounds(initialNext)) {
            wrap(dir, initialNext, board, wrapsDef)
        } else {
            Pair(dir, initialNext)
        }

        return if (board[wrapNext.second][wrapNext.first] == '#') {
            Pair(dir, current)
        } else {
            Pair(wrapDir, wrapNext)
        }
    }

    private fun wrap(
        dir: Direction,
        p: Pair<Int, Int>,
        board: List<List<Char>>,
        wrapsDef: List<Pair<Pair<Direction, IntProgression>, Pair<Direction, IntProgression>>>
    ): Pair<Direction, Pair<Int, Int>> {
        val (src, dest) = wrapsDef
            .single { (src, _) ->
                val (sd, rng) = src
                if (sd == dir) {
                    if (sd == Up || sd == Down) {
                        rng.contains(p.first)
                    } else {
                        rng.contains(p.second)
                    }
                } else {
                    false
                }
            }

        val idx = if (src.first == Up || src.first == Down) {
            src.second.indexOf(p.first)
        } else {
            src.second.indexOf(p.second)
        }


        val mappedElement = dest.second.elementAt(idx)

        val next = when (dest.first) {
            Up -> {
                val newY = board.map { it[mappedElement] }.indexOfLast { it != ' ' }
                Pair(mappedElement, newY)
            }

            Down -> {
                val newY = board.map { it[mappedElement] }.indexOfFirst { it != ' ' }
                Pair(mappedElement, newY)
            }

            Left -> {
                val newX = board[mappedElement].indexOfLast { it != ' ' }
                Pair(newX, mappedElement)
            }

            Right -> {
                val newX = board[mappedElement].indexOfFirst { it != ' ' }
                Pair(newX, mappedElement)
            }
        }
        return Pair(dest.first, next)
    }

    private fun nextDirection(current: Direction, turn: Turn): Direction {
        return when (current) {
            Up -> if (turn.dir == 'R') Right else Left
            Right -> if (turn.dir == 'R') Down else Up
            Down -> if (turn.dir == 'R') Left else Right
            Left -> if (turn.dir == 'R') Up else Down
        }
    }

    private fun determineStart(board: List<List<Char>>): Pair<Int, Int> {
        val i = board.first().indexOfFirst { it == '.' }

        return Pair(i, 0)
    }

    private fun parseInput(input: List<String>): Pair<MutableList<MutableList<Char>>, List<Action>> {
        val (baseBoard, moves) = input.asSequence().delimited { it == "" }.toList()

        val maxBoardWidth = baseBoard.maxOf { it.length }
        val board = baseBoard
            .map { it.padEnd(maxBoardWidth, ' ') }
            .map { it.toMutableList() }
            .toMutableList()

        val actions = moves.single().trim()
            .fold(mutableListOf<MutableList<Char>>()) { acc, c ->
                if (acc.isEmpty()) {
                    acc.add(mutableListOf(c))
                    acc
                } else {
                    val lastWasDigit = acc.last().last().isDigit()
                    val currentIsDigit = c.isDigit()

                    if (lastWasDigit && currentIsDigit) {
                        acc.last().add(c)
                    } else {
                        acc.add(mutableListOf(c))
                    }
                    acc
                }
            }
            .map {
                if (it.first().isDigit()) {
                    Move(it.joinToString("").toInt())
                } else {
                    Turn(it.single())
                }
            }

        return Pair(board, actions)
    }

    sealed interface Action
    data class Move(val cnt: Int) : Action
    data class Turn(val dir: Char) : Action

    sealed class Direction(val vert: Int, val horiz: Int, val sym: Char, val score: Int)
    object Up : Direction(-1, 0, '^', 3)
    object Down : Direction(1, 0, 'v', 1)
    object Left : Direction(0, -1, '<', 2)
    object Right : Direction(0, 1, '>', 0)

    private fun List<List<Char>>.outOfBounds(p: Pair<Int, Int>): Boolean {
        val (x, y) = p
        if (x < 0 || y < 0) {
            return true
        }

        if (x >= this.first().size || y >= this.size) {
            return true
        }

        if (this[y][x] == ' ') {
            return true
        }

        return false
    }

    private fun List<List<Char>>.print() {
        val map = this
            .map { it.joinToString("") }
            .joinToString("\n")

        println(map)
        println()
    }

    private fun Pair<Int, Int>.move(dir: Direction): Pair<Int, Int> {
        return Pair(this.first + dir.horiz, this.second + dir.vert)
    }

    private fun loadData(filename: String = "day22.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}