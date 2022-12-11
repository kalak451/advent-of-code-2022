import kotlin.test.Test
import kotlin.test.assertEquals

class Day11 {

    @Test
    fun part1() {
        val monkeys = buildPart1Monkeys()

        repeat(20) { runIteration(monkeys, 3L) }

        val (a, b) = monkeys.values.map { it.handleCount }.sortedDescending().take(2)

        val totalMonkeyBusiness = a * b

        assertEquals(57348, totalMonkeyBusiness)
    }

    @Test
    fun part2() {
        val monkeys = buildPart1Monkeys()

        repeat(10000) { runIteration(monkeys, 1L) }

        val (a, b) = monkeys.values.map { it.handleCount }.sortedDescending().take(2)

        val totalMonkeyBusiness = a * b

        assertEquals(14106266886, totalMonkeyBusiness)
    }

    fun runIteration(monkeys: Map<Int, Monkey>, worryReducer: Long) {
        val lcm = monkeys.map { it.value.testValue }.reduce { a, b -> a * b }
        (0..monkeys.keys.max()).forEach { monkeyIndex ->
            val currentMonkey = monkeys[monkeyIndex]!!
            currentMonkey.items.forEach { item ->
                val postHandleValue = (currentMonkey.operation(item) % lcm) / worryReducer
                val testResult = postHandleValue % currentMonkey.testValue == 0L
                if (testResult) {
                    monkeys[currentMonkey.testTrueMonkey]!!.items.add(postHandleValue)
                } else {
                    monkeys[currentMonkey.testFalseMonkey]!!.items.add(postHandleValue)
                }
                currentMonkey.handleCount++
            }
            currentMonkey.items.clear()
        }
    }

    fun buildPart1Monkeys(): Map<Int, Monkey> {
        return mapOf(
            Pair(
                0,
                Monkey(
                    items = mutableListOf(91, 58, 52, 69, 95, 54),
                    operation = { it * 13 },
                    testValue = 7L,
                    testTrueMonkey = 1,
                    testFalseMonkey = 5
                )
            ),
            Pair(
                1,
                Monkey(
                    items = mutableListOf(80, 80, 97, 84),
                    operation = { it * it },
                    testValue = 3L,
                    testTrueMonkey = 3,
                    testFalseMonkey = 5
                )
            ),
            Pair(
                2,
                Monkey(
                    items = mutableListOf(86, 92, 71),
                    operation = { it + 7 },
                    testValue = 2L,
                    testTrueMonkey = 0,
                    testFalseMonkey = 4
                )
            ),
            Pair(
                3,
                Monkey(
                    items = mutableListOf(96, 90, 99, 76, 79, 85, 98, 61),
                    operation = { it + 4 },
                    testValue = 11L,
                    testTrueMonkey = 7,
                    testFalseMonkey = 6
                )
            ),
            Pair(
                4,
                Monkey(
                    items = mutableListOf(60, 83, 68, 64, 73),
                    operation = { it * 19 },
                    testValue = 17L,
                    testTrueMonkey = 1,
                    testFalseMonkey = 0
                )
            ),
            Pair(
                5,
                Monkey(
                    items = mutableListOf(96, 52, 52, 94, 76, 51, 57),
                    operation = { it + 3 },
                    testValue = 5L,
                    testTrueMonkey = 7,
                    testFalseMonkey = 3
                )
            ),
            Pair(
                6,
                Monkey(
                    items = mutableListOf(75),
                    operation = { it + 5 },
                    testValue = 13L,
                    testTrueMonkey = 4,
                    testFalseMonkey = 2
                )
            ),
            Pair(
                7,
                Monkey(
                    items = mutableListOf(83, 75),
                    operation = { it + 1 },
                    testValue = 19L,
                    testTrueMonkey = 2,
                    testFalseMonkey = 6
                )
            )
        )
    }


    class Monkey(
        val items: MutableList<Long>,
        val operation: (Long) -> Long,
        val testValue: Long,
        val testTrueMonkey: Int,
        val testFalseMonkey: Int,
        var handleCount: Long = 0
    )
}