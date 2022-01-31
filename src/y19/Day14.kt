package y19

import kotlin.math.ceil
import kotlin.math.max

private const val day = 14
private const val trillion = 1_000_000_000_000L

private data class Reaction(
    val result: Pair<String, Long>,
    val reagents: List<Pair<String, Long>>
)

operator fun Pair<String, Long>.times(n: Long) = first to second * n

private fun Pair<String, Long>.requirements(
    reactions: Map<String, Reaction>
): List<Pair<String, Long>> {
    val reaction = reactions[first]!!
    val multiplier = second / reaction.result.second
    val rest = second % reaction.result.second
    return reaction.reagents.map { it * multiplier } + (first to rest)
}

private fun String.toChemical(): Pair<String, Long> {
    val (count, name) = split(" ")
    return name to count.toLong()
}

private fun String.toReaction(): Reaction {
    val (src, dst) = split(" => ")
    val reagents = src.split(", ").map { it.toChemical() }
    return Reaction(dst.toChemical(), reagents)
}

fun main() {

    fun processDown(
        goals: List<Pair<String, Long>>,
        reactions: Map<String, Reaction>
    ): List<Pair<String, Long>> {
        var current = goals
        while (current.any { it.first != "ORE" }) {
            val next = current
                .flatMap {
                    when (it.first) {
                        "ORE" -> listOf(it)
                        else -> it.requirements(reactions)
                    }
                }
                .groupBy { it.first }
                .map { (key, list) -> key to list.sumOf { it.second } }
                .filter { it.second > 0 }
            if (next == current)
                break
            current = next
        }
        return current
    }


    fun roundUp(
        curr: List<Pair<String, Long>>,
        reactions: Map<String, Reaction>,
        which: Set<String>
    ) = curr.map {
        val (name, count) = it
        val reaction = reactions[name]
        when {
            reaction == null -> it
            name in which -> reaction.result.second.let { n ->
                name to ceil(count.toDouble() / n).toLong() * n
            }
            else -> it
        }
    }

    fun reactionsAndDistances(input: List<String>): Pair<
            Map<String, Reaction>,
            Map<String, Long>
            > {
        val reactions: Map<String, Reaction> = input
            .map { it.toReaction() }
            .associateBy { it.result.first }
        val graphNodes = reactions.values
            .flatMap { it.reagents + it.result }
            .map { it.first }
            .distinct()
        val graphArcs = graphNodes.flatMap { n ->
            reactions[n]?.reagents?.map { n to it.first } ?: emptyList()
        }.distinct()
        val distances = mutableMapOf("FUEL" to 0L)
        repeat(graphArcs.size) {
            distances.toMap().forEach { (name, dist) ->
                val successors = graphArcs.filter { it.first == name }.map { it.second }
                successors.forEach { distances[it] = max(distances[it] ?: 0L, dist + 1) }
            }
        }
        return reactions to distances
    }

    fun getOreForFuel(
        reactions: Map<String, Reaction>,
        distances: Map<String, Long>,
        fuelGoal: Long
    ): Long {
        val goal = "FUEL" to fuelGoal
        var curr = listOf(goal)
        (0..distances.values.maxOf { it }).forEach { d ->
            curr = processDown(curr, reactions)
            curr = roundUp(curr, reactions, distances.filterValues { it == d }.keys)
        }
        return curr.sumOf { it.second }
    }

    fun part1(input: List<String>): Long =
        reactionsAndDistances(input).let { (reactions, distances) ->
            getOreForFuel(reactions, distances, 1)
        }

    fun part2(input: List<String>): Long {
        val (reactions: Map<String, Reaction>, distances) = reactionsAndDistances(input)
        var loFuel = 1L
        var hiFuel = trillion
        val cache = mutableMapOf<Long, Long>()
        while (loFuel + 1 < hiFuel) {
            val mdFuel = (loFuel + hiFuel) / 2
            val loOre = cache[loFuel] ?: getOreForFuel(reactions, distances, loFuel)
            val mdOre = cache[mdFuel] ?: getOreForFuel(reactions, distances, mdFuel)
            val hiOre = cache[hiFuel] ?: getOreForFuel(reactions, distances, hiFuel)
            cache[loFuel] = loOre
            cache[mdFuel] = mdOre
            cache[hiFuel] = hiOre
            when (trillion) {
                in loOre..mdOre -> hiFuel = mdFuel
                in mdOre..hiOre -> loFuel = mdFuel
            }
        }
        return loFuel
    }

    try {
        val testInput = readInput("Day${day}_test")
        val p1 = part1(testInput)
        val expected = 180697L
        check(p1 == expected) { "$p1 is not $expected" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
