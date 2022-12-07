import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day7 {
    @Test
    fun part1() {
        val root = buildRoot()

        val allDirectories = root.allChildren()

        val answer = allDirectories
            .filter { x -> x.size() <= 100000 }
            .sumOf { x -> x.size() }

        assertEquals(1783610, answer)
    }

    @Test
    fun part2() {
        val root = buildRoot()

        val currentRootSize = root.size()

        val totalSize = 70000000
        val neededSize = 30000000

        val emptySpace = totalSize - currentRootSize
        val needToDelete = neededSize - emptySpace

        val allDirectories = root.allChildren()
        val dirToDelete = allDirectories
            .filter { it.size() >= needToDelete }
            .minBy { it.size() }


        val answer = dirToDelete.size()
        assertEquals(4370655, answer)
    }

    private fun buildRoot(): DDirectory {
        val data = loadData()

        val root = DDirectory("/", null)

        var current = root
        data.asSequence()
            .split { it.startsWith("$") }
            .drop(1) //We don't care about the "cd /" command
            .forEach { lines ->
                val cmd = lines.first()
                val results = lines.drop(1)

                if (cmd.startsWith("$ ls")) {
                    current = processLsCommand(results, current)
                } else if (cmd.startsWith("$ cd")) {
                    current = processCdCommand(cmd, current)
                }
            }

        return root
    }

    private fun processCdCommand(cmd: String, current: DDirectory): DDirectory {
        val nextDir = cmd.substring(5)
        return if (nextDir == "..") {
            current.parent!!
        } else {
            current.children[nextDir]!!
        }
    }

    private fun processLsCommand(results: List<String>, current: DDirectory): DDirectory {
        results.forEach { r ->
            if (r.startsWith("dir ")) {
                val dName = r.substring(4)
                current.children[dName] = DDirectory(dName, current)
            } else {
                val (size, fName) = r.split(' ')
                current.files[fName] = DFile(fName, size.toLong())
            }
        }

        return current
    }

    data class DFile(val name: String, val size: Long)

    class DDirectory(
        val name: String,
        val parent: DDirectory?,
        val files: MutableMap<String, DFile> = mutableMapOf(),
        val children: MutableMap<String, DDirectory> = mutableMapOf(),
    ) {

        fun size(): Long {
            val fileSize = files.values.sumOf { it.size }
            val childSize = children.values.sumOf { it.size() }

            return fileSize + childSize
        }
        fun allChildren(): Sequence<DDirectory> {
            return children.values.asSequence() + children.values.asSequence().flatMap { c -> c.allChildren() }
        }
    }

    private fun loadData(): List<String> {
        return File(ClassLoader.getSystemResource("day7.txt").file).readLines()
    }
}