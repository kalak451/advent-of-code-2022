import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val results = processData(sample.map { it.toInt() })

        assertEquals(
            listOf(1, 2, -3, 4, 0, 3, -2),
            results
        )

        assertEquals(4, calculateNth(results, 1000))
        assertEquals(-3, calculateNth(results, 2000))
        assertEquals(2, calculateNth(results, 3000))
    }

    @Test
    fun part1() {
        val data = loadData()
        val results = processData(data.map { it.toInt() })

        val n1000 = calculateNth(results, 1000)
        val n2000 = calculateNth(results, 2000)
        val n3000 = calculateNth(results, 3000)

        assertEquals(0, n1000 + n2000 + n3000)
    }

    private fun calculateNth(data: List<Int>, nth: Int): Int {
        val indexOfZero = data.indexOf(0)

        val idx = ((indexOfZero + nth) % data.size)

        if(idx < 0) {
            return data[data.size - 1]
        }
        return data[idx]
    }

    private fun processData(originalData: List<Int>): List<Int> {
        val data = originalData
            .withIndex()
            .toMutableList()

        val size = data.size

//        println(data.map { it.value })

        for (i in 0 until size) {
            val currentIndex = data.indexOfFirst { it.index == i }
            val currentNode = data[currentIndex]
            val currentValue = currentNode.value

            val newIndex = calculateNewIndex(currentIndex, currentValue, size)

            doMove(data, currentIndex, newIndex)

//            println(data.map { it.value })
        }

        return data.map { it.value }
    }

    private fun <T> doMove(data: MutableList<T>, currentIndex: Int, newIndex: Int): List<T> {
        val currentNode = data[currentIndex]
        return if (newIndex == currentIndex) {
            data
        } else if (newIndex < currentIndex) {
            data.removeAt(currentIndex)
            data.add(newIndex, currentNode)
            data
        } else {
            data.add(newIndex + 1, currentNode)
            data.removeAt(currentIndex)
            data
        }
    }

    private fun calculateNewIndex(currentIndex: Int, move: Int, size: Int): Int {
        val cycledMove = move % size

        var x = currentIndex + cycledMove

        if(cycledMove < 0 && x <= 0) {
            x -= 1
        }

        if(cycledMove > 0 && x >= size - 1) {
            x += 1
        }


        if(x < 0) {
            x += size
        }

        if(x >= size) {
            x -= size
        }

        return x
    }

    private fun loadData(filename: String = "day20.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }

    @Test
    fun testMove() {
        assertEquals(
            listOf(2, 1, -3, 3, -2, 0, 4),
            doMove(mutableListOf(1, 2, -3, 3, -2, 0, 4), 0, 1)
        )
        assertEquals(
            listOf(1, -3, 2, 3, -2, 0, 4),
            doMove(mutableListOf(2, 1, -3, 3, -2, 0, 4), 0, 2)
        )
        assertEquals(
            listOf(1, 2, 3, -2, -3, 0, 4),
            doMove(mutableListOf(1, -3, 2, 3, -2, 0, 4), 1, 4)
        )
        assertEquals(
            listOf(1, 2, -2, -3, 0, 3, 4),
            doMove(mutableListOf(1, 2, 3, -2, -3, 0, 4), 2, 5)
        )
        assertEquals(
            listOf(1, 2, -3, 0, 3, 4, -2),
            doMove(mutableListOf(1, 2, -2, -3, 0, 3, 4), 2, 6)
        )
        assertEquals(
            listOf(1, 2, -3, 0, 3, 4, -2),
            doMove(mutableListOf(1, 2, -3, 0, 3, 4, -2), 3, 3)
        )
        assertEquals(
            listOf(1, 2, -3, 4, 0, 3, -2),
            doMove(mutableListOf(1, 2, -3, 0, 3, 4, -2), 5, 3)
        )
    }



    @Test
    fun testWrapping() {
//        assertEquals(1, calculateNewIndex(0, 1, 7))
//        assertEquals(2, calculateNewIndex(0, 2, 7))
//        assertEquals(4, calculateNewIndex(1, -3, 7))
//        assertEquals(5, calculateNewIndex(2, 3, 7))
//        assertEquals(6, calculateNewIndex(2, -2, 7))
//        assertEquals(3, calculateNewIndex(3, 0, 7))
//        assertEquals(3, calculateNewIndex(5, 4, 7))

        assertEquals(5, calculateNewIndex(5, 14, 7))
        assertEquals(4, calculateNewIndex(5, 13, 7))

//        assertEquals(5, calculateNewIndex(5, -14, 7))
//        assertEquals(6, calculateNewIndex(5, -13, 7))
    }
}