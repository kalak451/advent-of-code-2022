import java.io.File
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals

class Day19 {
    private val sample = """
        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    """.trimIndent().split("\n")

    @Test
    fun part1Partial() {
        val blueprints = loadBlueprints(sample)

        val result = calculateMaxGeodes(blueprints[0], 24)

        assertEquals(9, result)
    }

    @Test
    fun part1Sample() {
        val blueprints = loadBlueprints(sample)

        val answer = blueprints.sumOf { bp -> calculateMaxGeodes(bp, 24) * bp.id }

        assertEquals(33, answer)
    }

    @Test
    fun part1() {
        val blueprints = loadBlueprints(loadData())

        val answer = blueprints.map { bp -> calculateMaxGeodes(bp, 24) * bp.id }

        assertEquals(1834, answer.sum())
    }

    @Test
    fun part2() {
        val blueprints = loadBlueprints(loadData()).asSequence().take(3).toList()

        val (a1,a2,a3) = blueprints.map { bp -> calculateMaxGeodes(bp, 32) }.toList()

        assertEquals(2240,a1 * a2 * a3)
    }

    private fun calculateMaxGeodes(
        blueprint: Blueprint,
        time: Int,
        storedAmounts: List<Long> = List(4) { 0 },
        robots: List<Long> = List(4) { if (it == 0) 1 else 0 },
        cache: MutableMap<List<Any>, Long> = mutableMapOf()
    ): Long {
        if (time == 0) {
            return storedAmounts[3]
        }

        val cacheKey = listOf(time, storedAmounts, robots)
        if (cache.containsKey(cacheKey)) {
            return cache[cacheKey]!!
        }

        val doNothing = storedAmounts[3] + (robots[3] * time)

        val answers = mutableListOf<Long>()
        answers.add(doNothing)

        val oreWait = blueprint.oreRobotRecipe.maxTimeToBuild(storedAmounts, robots)
        if (time - oreWait > 0 && robots[0] < blueprint.maxNeededOre()) {
            answers.add(
                calculateMaxGeodes(
                    blueprint,
                    time - oreWait,
                    calculateNextAmount(storedAmounts, robots, blueprint.oreRobotRecipe, oreWait),
                    listOf(robots[0] + 1, robots[1], robots[2], robots[3]),
                    cache
                )
            )
        }

        val clayWait = blueprint.clayRobotRecipe.maxTimeToBuild(storedAmounts, robots)
        if (time - clayWait > 0 && robots[0] < blueprint.maxNeededClay()) {
            answers.add(
                calculateMaxGeodes(
                    blueprint,
                    time - clayWait,
                    calculateNextAmount(storedAmounts, robots, blueprint.clayRobotRecipe, clayWait),
                    listOf(robots[0], robots[1] + 1, robots[2], robots[3]),
                    cache
                )
            )
        }

        val obsidianWait = blueprint.obsidianRobotRecipe.maxTimeToBuild(storedAmounts, robots)
        if (time - obsidianWait > 0 && robots[0] < blueprint.maxNeededObsidian()) {
            answers.add(
                calculateMaxGeodes(
                    blueprint,
                    time - obsidianWait,
                    calculateNextAmount(storedAmounts, robots, blueprint.obsidianRobotRecipe, obsidianWait),
                    listOf(robots[0], robots[1], robots[2] + 1, robots[3]),
                    cache
                )
            )
        }

        val geodeWait = blueprint.geodeRobotRecipe.maxTimeToBuild(storedAmounts, robots)
        if (time - geodeWait > 0) {
            answers.add(
                calculateMaxGeodes(
                    blueprint,
                    time - geodeWait,
                    calculateNextAmount(storedAmounts, robots, blueprint.geodeRobotRecipe, geodeWait),
                    listOf(robots[0], robots[1], robots[2], robots[3]+1),
                    cache
                )
            )
        }

        val answer = answers.maxOrNull() ?: storedAmounts[3]
        cache[cacheKey] = answer

        return answer
    }

    private fun calculateNextAmount(current: List<Long>, robots: List<Long>, recipe: RobotRecipe?): List<Long> {
        val (cOre, cClay, cOb, cGeode) = current
        val (rOre, rClay, rOb, rGeode) = robots

        return if (recipe == null) {
            listOf(
                cOre + rOre,
                cClay + rClay,
                cOb + rOb,
                cGeode + rGeode
            )
        } else {
            listOf(
                cOre + rOre - recipe.ore,
                cClay + rClay - recipe.clay,
                cOb + rOb - recipe.obsidian,
                cGeode + rGeode
            )
        }
    }

    private fun calculateNextAmount(
        current: List<Long>,
        robots: List<Long>,
        recipe: RobotRecipe,
        wait: Int
    ): List<Long> {
        val (cOre, cClay, cOb, cGeode) = current
        val (rOre, rClay, rOb, rGeode) = robots

        return listOf(
            cOre + (rOre * wait) - recipe.ore,
            cClay + (rClay * wait) - recipe.clay,
            cOb + (rOb * wait) - recipe.obsidian,
            cGeode + (rGeode * wait)
        )
    }

    private fun loadBlueprints(input: List<String>): List<Blueprint> {
        return input
            .map { it.allInts() }
            .map {
                val id = it[0]
                val oreOre = it[1]
                val clayOre = it[2]
                val obsidianOre = it[3]
                val obsidianClay = it[4]
                val geodeOre = it[5]
                val geodeObsidian = it[6]

                Blueprint(
                    id,
                    RobotRecipe(oreOre, 0, 0),
                    RobotRecipe(clayOre, 0, 0),
                    RobotRecipe(obsidianOre, obsidianClay, 0),
                    RobotRecipe(geodeOre, 0, geodeObsidian)
                )
            }
    }

    data class RobotRecipe(val ore: Int, val clay: Int, val obsidian: Int) {
        fun canBuild(components: List<Long>): Boolean {
            val (aOre, aClay, aOb) = components
            return (aOre >= ore && aClay >= clay && aOb >= obsidian)
        }

        fun maxTimeToBuild(components: List<Long>, robots: List<Long>): Int {
            val timeForOre: Int =
                if (ore == 0) 0 else (if (robots[0] == 0L) Int.MAX_VALUE else ((ore - components[0] - 1 + robots[0]) / robots[0])).toInt()
            val timeForClay: Int =
                if (clay == 0) 0 else (if (robots[1] == 0L) Int.MAX_VALUE else ((clay - components[1] - 1 + robots[1]) / robots[1])).toInt()
            val timeForObsidian: Int =
                if (obsidian == 0) 0 else (if (robots[2] == 0L) Int.MAX_VALUE else ((obsidian - components[2] - 1 + robots[2]) / robots[2])).toInt()
            return max(max(timeForOre, timeForClay), timeForObsidian) + 1
        }
    }

    data class Blueprint(
        val id: Int,
        val oreRobotRecipe: RobotRecipe,
        val clayRobotRecipe: RobotRecipe,
        val obsidianRobotRecipe: RobotRecipe,
        val geodeRobotRecipe: RobotRecipe
    ) {
        fun maxNeededOre(): Int {
            return listOf(
                oreRobotRecipe.ore,
                clayRobotRecipe.ore,
                obsidianRobotRecipe.ore,
                geodeRobotRecipe.ore
            ).max()
        }

        fun maxNeededClay(): Int {
            return listOf(
                oreRobotRecipe.clay,
                clayRobotRecipe.clay,
                obsidianRobotRecipe.clay,
                geodeRobotRecipe.clay
            ).max()
        }

        fun maxNeededObsidian(): Int {
            return listOf(
                oreRobotRecipe.obsidian,
                clayRobotRecipe.obsidian,
                obsidianRobotRecipe.obsidian,
                geodeRobotRecipe.obsidian
            ).max()
        }
    }

    private fun loadData(filename: String = "day19.txt"): List<String> {
        return File(ClassLoader.getSystemResource(filename).file).readLines()
    }
}