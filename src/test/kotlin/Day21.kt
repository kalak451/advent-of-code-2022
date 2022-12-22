import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class Day21 {

    val sample = """
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32
    """.trimIndent().split("\n")

    @Test
    fun part1Sample() {
        val monkeys = parse(sample)

        assertEquals(152, eval("root", monkeys))
    }

    @Test
    fun part1() {
        val monkeys = parse(loadData())

        assertEquals(286698846151845, eval("root", monkeys))
    }

    @Test
    fun part2Sample() {
        val monkeys = parse(sample)

        assertEquals(301, solve("root", monkeys))
    }

    @Test
    fun part2() {
        val monkeys = parse(loadData())

        assertEquals(3759566892641, solve("root", monkeys))
    }

    private fun solve(id: String, monkeys: Map<String, Monkey>): Long {
        val eqOp = monkeys[id]!! as Op

        val (x, y) = if (contains("humn", eqOp.a, monkeys)) {
            Pair(eqOp.a, eqOp.b)
        } else {
            Pair(eqOp.b, eqOp.a)
        }

        val solution = eval(y, monkeys)

        return solve(x, solution, monkeys)
    }

    private fun solve(id: String, solution: Long, monkeys: Map<String, Monkey>): Long {
        if (id == "humn") {
            return solution
        }

        val op = monkeys[id]!! as Op

        val (x, y) = if (contains("humn", op.a, monkeys)) {
            Pair(op.a, op.b)
        } else {
            Pair(op.b, op.a)
        }

        val yVal = eval(y, monkeys)
        if (op.op == '+') {
            return solve(x, solution - yVal, monkeys)
        }

        if (op.op == '-') {
            if (op.a == x) {
                return solve(x, solution + yVal, monkeys)
            } else {
                return solve(x, yVal - solution, monkeys)
            }
        }

        if (op.op == '*') {
            return solve(x, solution / yVal, monkeys)
        }

        if (op.op == '/') {
            if (op.a == x) {
                return solve(x, solution * yVal, monkeys)
            } else {
                return solve(x, yVal / solution, monkeys)
            }
        }

        throw RuntimeException("Err")
    }

    private fun contains(search: String, current: String, monkeys: Map<String, Monkey>): Boolean {
        return when (val monkey = monkeys[current]!!) {
            is Value -> current == search
            is Op -> contains(search, monkey.a, monkeys) || contains(search, monkey.b, monkeys)
        }
    }

    private fun eval(id: String, monkeys: Map<String, Monkey>): Long {
        return when (val monkey = monkeys[id]!!) {
            is Value -> monkey.v
            is Op -> monkey.func.invoke(eval(monkey.a, monkeys), eval(monkey.b, monkeys))
        }
    }

    private fun parse(lines: List<String>): Map<String, Monkey> {
        return lines.associate { parse(it) }
    }

    private fun parse(line: String): Pair<String, Monkey> {
        val id = line.substring(0..3)
        val rest = line.substring(6 until line.length)

        if (rest[0].isDigit()) {
            return Pair(id, Value(rest.toLong()))
        }

        val a = rest.substring(0..3)
        val b = rest.substring(7..10)

        val op = when (val opChar = rest[5]) {
            '+' -> Op(a, b, '+') { x, y -> x + y }
            '-' -> Op(a, b, '-') { x, y -> x - y }
            '*' -> Op(a, b, '*') { x, y -> x * y }
            '/' -> Op(a, b, '/') { x, y -> x / y }
            else -> throw RuntimeException("Unexpected Op: $opChar")
        }

        return Pair(id, op)
    }

    sealed interface Monkey
    data class Value(val v: Long) : Monkey
    data class Op(val a: String, val b: String, val op: Char, val func: (Long, Long) -> Long) : Monkey

    private fun loadData(filename: String = "day21.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}