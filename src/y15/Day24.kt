package y15

private const val day = "24"

fun main() {
    fun List<Int>.findSetsSummingTo(total: Int): List<Set<Int>> =
        flatMapIndexed { i, v ->
            if (v > total) emptyList()
            else if (v == total) listOf(setOf(v))
            else drop(i + 1).findSetsSummingTo(total - v).map { it + v }
        }

    fun Collection<Int>.multiply(): Long = fold(1L) { a, v -> a * v }

    fun isValid(
        group: Set<Int>,
        allGroups: List<Set<Int>>,
        allWeights: List<Int>,
        groupCount: Int
    ): Boolean = when (groupCount) {
        1 -> group in allGroups
        2 -> (allWeights - group).toSet() in allGroups
        in 3..99 -> {
            val restWeights = allWeights - group
            val restSets: List<Set<Int>> = (restWeights).findSetsSummingTo(group.sum())
            restSets.any { isValid(it, restSets, restWeights, groupCount - 1) }
        }
        else -> throw IllegalArgumentException()
    }

    fun optimize(input: List<String>, groupCount: Int): Long {
        val weights = input.map { it.toInt() }.sortedDescending()
        val total = weights.sum()
        val goal = total / groupCount
        println("Total $total = $groupCount * $goal")

        val groups = weights
            .findSetsSummingTo(goal)
            .sortedBy { it.size }
        println("Valid groups: ${groups.size}")

        val smallest = groups.first { isValid(it, groups, weights, groupCount) }
        println("Smallest size is ${smallest.size} (e.g. $smallest)")

        val startQuantum = smallest.multiply()
        val allSmall = groups
            .filter { it.size == smallest.size }
            .filter { it.multiply() <= startQuantum }
        println("All small ${allSmall.size} (e.g. ${allSmall.take(5)})")

        return allSmall.fold(startQuantum) { min, curr ->
            val quantum = curr.multiply()
            if (quantum < min && isValid(curr, groups, weights, groupCount)) quantum
            else min
        }
    }

    val input = readInput("Day${day}")
    println(optimize(input, groupCount = 3))
    println(optimize(input, groupCount = 4))
}
