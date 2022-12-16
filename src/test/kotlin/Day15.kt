import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals

class Day15 {

    val sampleData = """
            Sensor at x=2, y=18: closest beacon is at x=-2, y=15
            Sensor at x=9, y=16: closest beacon is at x=10, y=16
            Sensor at x=13, y=2: closest beacon is at x=15, y=3
            Sensor at x=12, y=14: closest beacon is at x=10, y=16
            Sensor at x=10, y=20: closest beacon is at x=10, y=16
            Sensor at x=14, y=17: closest beacon is at x=10, y=16
            Sensor at x=8, y=7: closest beacon is at x=2, y=10
            Sensor at x=2, y=0: closest beacon is at x=2, y=10
            Sensor at x=0, y=11: closest beacon is at x=2, y=10
            Sensor at x=20, y=14: closest beacon is at x=25, y=17
            Sensor at x=17, y=20: closest beacon is at x=21, y=22
            Sensor at x=16, y=7: closest beacon is at x=15, y=3
            Sensor at x=14, y=3: closest beacon is at x=15, y=3
            Sensor at x=20, y=1: closest beacon is at x=15, y=3
        """.trimIndent().split("\n")

    @Test
    fun part1Sample() {
        val targetY = 10
        val numbers = sampleData.map { it.allInts().chunked(2) }

        val answer = doPart1(numbers, targetY)

        assertEquals(26, answer)
    }

    @Test
    fun part1() {
        val targetY = 2_000_000
        val data = loadData()
        val numbers = data.map { it.allInts().chunked(2) }

        val answer = doPart1(numbers, targetY)

        assertEquals(4886370, answer)
    }

    @Test
    fun part2Sample() {
        val maxX = 20
        val numbers = sampleData.map { it.allInts().chunked(2) }
        val answer = doPart2(maxX, numbers)

        assertEquals(56000011, answer)
    }

    @Test
    fun part2() {
        val maxX = 4_000_000
        val data = loadData()
        val numbers = data.map { it.allInts().chunked(2) }
        val answer = doPart2(maxX, numbers)

        assertEquals(11374534948438, answer)
    }

    private fun doPart2(
        maxX: Int,
        numbers: List<List<List<Int>>>
    ): Long {
        val (y, xRanges) = (0..maxX)
            .asSequence()
            .map { extractRanges(numbers, it) }
            .map {
                it.map { r -> r.rangeIntersect(0..maxX) }
            }
            .map { it.sortedBy { r -> r.first } }
            .withIndex()
            .filter { it.value.any { r -> r != (0..maxX) } }
            .filter {
                it.value.windowed(2).any { (a, b) -> b.first - a.last > 1 }
            }.single()

        val (before) = xRanges.windowed(2).single { (a, b) -> b.first - a.last == 2 }
        val x = before.last + 1

        return (x * 4_000_000L) + y
    }

    private fun doPart1(
        numbers: List<List<List<Int>>>,
        targetY: Int
    ): Int {
        return extractRanges(numbers, targetY).sumOf { it.last - it.first }
    }

    private fun extractRanges(
        numbers: List<List<List<Int>>>,
        targetY: Int
    ): List<IntRange> {
        return numbers
            .asSequence()
            .map { (sensor, beacon) ->
                val (sx, sy) = sensor
                val (bx, by) = beacon

                val dist = abs(sx - bx) + abs(sy - by)

                Pair(Pair(sx, sy), dist)
            }.mapNotNull { (sensor, dist) ->
                val (sx, sy) = sensor
                val yDistToTarget = abs(targetY - sy)

                if (yDistToTarget > dist) {
                    null
                } else {
                    sx - dist + yDistToTarget..sx + dist - yDistToTarget
                }
            }
            .fold(mutableListOf()) { acc, r ->
                rangeFolder(acc, r)
            }
    }

    private fun rangeFolder(
        acc: MutableList<IntRange>,
        r: IntRange
    ): MutableList<IntRange> {
        if (acc.isEmpty()) {
            acc.add(r)
            return acc
        }

        var overlapped = false

        for (idx in acc.indices) {
            val a = acc[idx]
            if(!a.rangeIntersect(r).isEmpty()) {
                overlapped = true
                acc[idx] = a.rangeUnion(r)
            }
        }

        return if (overlapped) {
            acc.fold(mutableListOf()) { a, b -> rangeFolder(a, b) }
        } else {
            acc.add(r)
            acc
        }
    }


    fun IntRange.rangeIntersect(r: IntRange): IntRange {
        val maxFirst = max(this.first, r.first)
        val minLast = min(this.last, r.last)

        if (this.contains(maxFirst) && r.contains(maxFirst)) {
            if (this.contains(minLast) && r.contains(minLast)) {
                return maxFirst..minLast
            }
        }

        return IntRange.EMPTY
    }

    fun IntRange.rangeUnion(r: IntRange): IntRange {
        val minFirst = min(this.first, r.first)
        val maxLast = max(this.last, r.last)

        return minFirst..maxLast
    }

    private fun loadData(filename: String = "day15.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}