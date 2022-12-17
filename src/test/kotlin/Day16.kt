import java.io.File
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals

class Day16 {

    val sample1 = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent().split("\n")

    @Test
    fun part1Sample() {
        val nodes = loadNodes(sample1)

        val answer = doPart1(nodes)

        assertEquals(1651, answer)
    }

    @Test
    fun part1() {
        val nodes = loadNodes(loadData())

        val answer = doPart1(nodes)

        assertEquals(1737, answer)
    }

    @Test
    fun part2Sample() {
        val nodes = loadNodes(sample1)

        val answer = doPart2(nodes)

        assertEquals(1707, answer)
    }

    @Test
    fun part2() {
        val nodes = loadNodes(loadData())

        val answer = doPart2(nodes)

        assertEquals(2216, answer)
    }

    private fun doPart1(nodes: Map<String, Node>): Long {
        val distances = calcDistances(nodes)
        val valves = nodes
            .filter { (k, v) -> v.rate > 0 }
            .map { it.key }
            .toSet()
        return find(30, "AA", valves, false, distances, nodes, mutableMapOf())
    }

    private fun doPart2(nodes: Map<String, Node>): Long {
        val distances = calcDistances(nodes)
        val valves = nodes
            .filter { (k, v) -> v.rate > 0 }
            .map { it.key }
            .toSet()
        return find(26, "AA", valves, true, distances, nodes, mutableMapOf())
    }

    private fun find(
        timeRemaining: Int,
        current: String,
        valves: Set<String>,
        useSecond: Boolean,
        dists: Map<Pair<String, String>, Int>,
        nodes: Map<String, Node>,
        memo: MutableMap<List<Any>, Long>
    ): Long {
        val memoKey = listOf(timeRemaining, current, valves, useSecond)
        if(memo.containsKey(memoKey)) {
            return memo[memoKey]!!
        }

        val pValues = valves
            .filter { v -> dists[Pair(current, v)]!! < timeRemaining }
            .map { v ->
                val tv = timeRemaining - dists[Pair(current, v)]!! - 1
                (nodes[v]!!.rate * tv) + find(tv, v, valves.minus(v), useSecond, dists, nodes, memo)
            }

        val eValue = if (useSecond) {
            find(26, "AA", valves, false, dists, nodes, memo)
        } else {
            0L
        }

        val result = (pValues + listOf(eValue)).max()
        memo[memoKey] = result
        return result
    }

    private fun calcDistances(nodes: Map<String, Node>): Map<Pair<String, String>, Int> {
        val dist = mutableMapOf<Pair<String, String>, Int>()

        nodes.forEach { k, v ->
            dist[Pair(k, k)] = 0
            v.connections.forEach { c ->
                dist[Pair(k, c)] = 1
            }
        }

        nodes.keys.forEach { k ->
            nodes.keys.forEach { i ->
                nodes.keys.forEach { j ->
                    val c = dist.getOrDefault(Pair(i, j), 9999)
                    dist[Pair(i, j)] = min(c, dist.getOrDefault(Pair(i, k), 9999) + dist.getOrDefault(Pair(k, j), 9999))
                }
            }
        }


        return dist.toMap()
    }

    private fun calculateTurn(valvesOn: Set<String>, nodes: Map<String, Node>): Long {
        return valvesOn.sumOf { nodes[it]!!.rate }
    }

    private fun loadNodes(lines: List<String>): Map<String, Node> {
        val s1Regex = Regex("^Valve (.*) has flow rate=(.*);.*$")
        val s2Regex = Regex("^.*tunnels* leads* to valves* (.*)\$")

        return lines.associate { l ->
            val s1Match = s1Regex.matchEntire(l)!!
            val valveName = s1Match.groupValues[1]
            val rate = s1Match.groupValues[2].toLong()

            val s2Match = s2Regex.matchEntire(l)!!
            val connectionsString = s2Match.groupValues[1]
            val connections = connectionsString.split(", ")

            Pair(valveName, Node(rate, connections))
        }

    }

    data class Node(val rate: Long, val connections: List<String>)

    private fun loadData(filename: String = "day16.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }

    sealed interface TurnOption

    data class Move(val node: String) : TurnOption
    object Open : TurnOption
}