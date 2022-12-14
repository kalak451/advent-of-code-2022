import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class Day13 {
    @Test
    fun part1() {
        val data = loadData().asSequence().delimited { it == "" }

        val answer = data
            .map { (a, b) -> listOf(parse(a), parse(b)) }
            .withIndex()
            .map { iv -> IndexedValue(iv.index + 1, doCompare(iv.value[0], iv.value[1])) }
            .filter { it.value == 1 }
            .map { it.index }
            .sum()

        assertEquals(5806, answer)
    }

    @Test
    fun part2() {
        val data = loadData().filter { it != "" } + listOf("[[2]]", "[[6]]")

        val sortedData = data
            .map { parse(it) }
            .sortedWith( this::doCompare )
            .reversed()

        val p1 = sortedData.indexOf(listOf(listOf(2))) + 1
        val p2 = sortedData.indexOf(listOf(listOf(6))) + 1

        assertEquals(23600, p1 * p2)
    }

    private fun parse(input: String): Any {
        var acc = mutableListOf<Char>()
        var current = mutableListOf<Any>()
        val stk = Stack<MutableList<Any>>()

        input.forEach { c ->
            if(c.isDigit()) {
                acc.add(c)
            } else if(acc.isNotEmpty()) {
                val i = acc.joinToString("").toInt()
                current.add(i)
                acc = mutableListOf()
            }

            if(c == '[') {
                stk.push(current)
                current = mutableListOf()
                acc = mutableListOf()
            } else if(c == ']') {
                val p = stk.pop()!!
                p.add(current)
                current = p
                acc = mutableListOf()
            }
        }

        if(acc.isNotEmpty()) {
            return acc.joinToString("").toInt()
        } else {
            return current.first()
        }

    }

    private fun doCompare(left: Any, right: Any): Int {
        if(left is Int && right is Int) {
            return if(left < right) {
                1
            } else if(left > right) {
                -1
            } else {
                0
            }
        }

        if(left is Int) {
            return doCompare(listOf(left), right)
        }

        if(right is Int) {
            return doCompare(left, listOf(right))
        }

        if(left is List<*> && right is List<*>) {
            var i = 0
            while(i < left.size && i < right.size) {
                val x = doCompare(left[i]!!, right[i]!!)
                if(x != 0) {
                    return x
                }
                i++
            }

            return if(left.size == right.size) {
                0
            } else if(left.size < right.size) {
                1
            } else {
                -1
            }
        }

        throw Exception("Unexpected input!")
    }

    private fun loadData(filename: String = "day13.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }

    @Test
    fun parseTests() {
        assertEquals(9, parse("9"))
        assertEquals(listOf<Int>(), parse("[]"))
        assertEquals(listOf(3), parse("[3]"))
        assertEquals(listOf(10), parse("[10]"))
        assertEquals(listOf(1, 1, 3, 1, 1), parse("[1,1,3,1,1]"))
        assertEquals(listOf(listOf(1), listOf(2, 3, 4)), parse("[[1],[2,3,4]]"))
        assertEquals(listOf(listOf(1), 4), parse("[[1],4]"))
        assertEquals(listOf(listOf(8,7,6)), parse("[[8,7,6]]"))
    }
}