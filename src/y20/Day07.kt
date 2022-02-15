package y20

private const val day = "07"

fun main() {
    fun String.toRule(): Pair<String, Map<String, Int>> {
        val (container, content) = split(" bags contain ")
        val map = when {
            "no other" in content -> mapOf()
            else -> {
                val parts = content.split(""" bags?[.,] ?""".toRegex()).filter { it.isNotBlank() }
                parts.map { it.split(" ", limit = 2) }.associate { (n, t) -> t to n.toInt() }
            }
        }
        return container to map
    }

    fun part1(input: List<String>): Int {
        val rules = input.associate { it.toRule() }

        val containers = mutableSetOf<String>()
        var last = setOf("shiny gold")
        while (last.isNotEmpty()) {
            val parents = last.flatMap {
                rules.filterValues { rule -> rule.containsKey(it) }.keys
            }
            last = parents.toSet() - containers
            containers.addAll(last)
        }
        return containers.size
    }

    fun part2(input: List<String>): Int {
        val rules = input.associate { it.toRule() }.filterValues { it.isNotEmpty() }
        val initialBags = listOf("shiny gold" to 1)
        val (_, partial) = generateSequence(initialBags to 0) { (bags, total) ->
            val step = bags.flatMap { (bag, n) -> (rules[bag] ?: mapOf()).mapValues { (_, v) -> v * n }.toList() }
            step to total + bags.sumOf { it.second }
        }.first { it.first.isEmpty() }
        return partial - initialBags.sumOf { it.second }
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 246)
    val p2 = part2(input)
    println(p2)
    check(p2 == 2976)
}
