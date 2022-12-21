import java.io.File
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

private const val i1 = 811589153

class Day20 {

    val sample = """
        1
        2
        -3
        3
        -2
        0
        4
    """.trimIndent().split("\n")

    @Test
    fun part1Sample() {
        val nodes = toNodes(sample.map { it.toLong() })
        runCycle(nodes)

        val zeroNode = nodes.find { it.value == 0L }!!

        val answers = findAnswers(zeroNode)

        assertEquals(3, answers.sumOf { nodes[it].value })
    }

    @Test
    fun part1() {
        val nodes = toNodes(loadData().map { it.toLong() })
        runCycle(nodes)

        val zeroNode = nodes.find { it.value == 0L }!!

        val answers = findAnswers(zeroNode)

        assertEquals(10763, answers.sumOf { nodes[it].value })
    }

    @Test
    fun part2Sample() {
        val key = 811589153
        val original = sample.map { it.toLong() }
        val nodes = toNodes(original.map { (it * key) % (original.size - 1) })
        repeat(10) { runCycle(nodes) }

        val zeroNode = nodes.find { it.value == 0L }!!

        val answers = findAnswers(zeroNode)

        assertEquals(1623178306, answers.sumOf { original[it] * key })

    }

    @Test
    fun part2() {
        val key = 811589153

        val original = loadData().map { it.toLong() }
        val nodes = toNodes(original.map { (it * key) % (original.size - 1) })
        val zeroNode = nodes[original.indexOf(0)]

        repeat(10) { runCycle(nodes) }

        val answers = findAnswers(zeroNode)

        assertEquals(4979911042808, answers.sumOf { original[it] * key })
    }

    private fun findAnswers(zeroNode: Node): List<Int> {
        val answers = mutableListOf<Int>()
        var current = zeroNode
        (1..3000).forEach { n ->
            current = current.next
            if (n % 1000 == 0) {
                answers.add(current.origIdx)
            }
        }
        return answers
    }

    private fun runCycle(nodes: List<Node>) {
        nodes.forEach { node ->
            repeat(abs(node.value).toInt()) {
                if (node.value < 0) {
                    switchNodes(node.prev, node)
                } else {
                    switchNodes(node, node.next)
                }
            }
        }
    }

    private fun switchNodes(a: Node, b: Node) {
        val pp = a.prev
        val nn = b.next

        pp.next = b
        b.prev = pp

        b.next = a
        a.prev = b

        a.next = nn
        nn.prev = a
    }

    private fun toNodes(input: List<Long>): List<Node> {
        val nodes = input.mapIndexed { idx, i -> Node(idx, i) }
        nodes.windowed(2).forEach { (a, b) ->
            a.next = b
            b.prev = a
        }

        nodes.first().prev = nodes.last()
        nodes.last().next = nodes.first()

        return nodes
    }

    data class Node( val origIdx: Int, var value: Long) {
        lateinit var next: Node
        lateinit var prev: Node
    }

    private fun loadData(filename: String = "day20.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}