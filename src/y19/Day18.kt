package y19

private const val day = 18

private operator fun String.minus(c: Char) = first { it != c }

fun main() {
    fun Map<Pair<Int, Int>, Char>.neighbors(pos: Pair<Int, Int>): List<Pair<Int, Int>> {
        val c = this[pos]
        val (x, y) = pos
        return when (c) {
            '.' -> listOf(x + 1 to y, x - 1 to y, x to y - 1, x to y + 1).filter { it in this }
            else -> emptyList()
        }
    }

    fun part1(input: List<String>): Int {
        val grid = input.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c != '#') (x to y) to c
                else null
            }
        }.toMap()

        val poi: Set<Pair<Int, Int>> = grid.filterValues { it == '@' || it.lowercaseChar() in 'a'..'z' }.keys
//        println(grid.filterKeys { it in poi })

        val allDistances: Map<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int> = poi
            .associateWith { start: Pair<Int, Int> ->
                minDistances(start) { (grid + (start to '.')).neighbors(it) }.filter { (k, _) -> k in poi - start }
            }
            .toList()
            .flatMap { (from, tos) -> tos.map { (k, v) -> (from to k) to v } }
            .toMap()

        val arcs: Map<String, Int> = allDistances.map { (x, cost) ->
            val (a, b) = x
            "${grid[a]}${grid[b]}".toSortedSet().joinToString("") to cost
        }.toMap()

        val nodes: List<Char> = arcs.keys.flatMap { it.toList() }.distinct()
        val keys = nodes.filter { it in 'a'..'z' }

        fun neighbors(c: Char): Map<Char, Int> =
            arcs.filter { c in it.key }.mapKeys { it.key - c }

        fun hash(path: String) = "${path.toSortedSet().toList()}->${path.last()}"

        println(arcs)
        var candidates = mapOf("@" to 0)
        val hashToCost = candidates.mapKeys { (k, _) -> hash(k) }.toMutableMap()
        val hashToPath = candidates.keys.associateBy { hash(it) }.toMutableMap()
        var j = 0
        while (candidates.isNotEmpty()) {
            val nextStates: List<Pair<String, Int>> = candidates.flatMap { (path, cost) ->
                val tail = path.last()
                val availableSteps = neighbors(tail).filterKeys { it !in 'A'..'Z' || it.lowercaseChar() in path }
                availableSteps.map { (node, dist) -> path + node to cost + dist }
            }
            candidates = nextStates
                .groupBy { (path) -> hash(path) }
                .mapNotNull { (h, equivalentStates) ->
                    val currentBest: Pair<String, Int>? = equivalentStates.minByOrNull { (_, cost) -> cost }
                    when {
                        currentBest == null -> null
                        currentBest.second >= (hashToCost[h] ?: Int.MAX_VALUE) -> null
                        else -> currentBest
                    }
                }
                .toMap()
            candidates.forEach { (path, cost) ->
                val h = hash(path)
                hashToCost[h] = cost
                hashToPath[h] = path
            }
            println("| ${++j} > ${candidates.size} <")
        }

        return hashToCost.filter { (h) -> h.toSet().containsAll(keys) }.minOf { (_, cost) -> cost }
    }


    fun part2(input: List<String>): Int {
        return input.size
    }

    try {
        val testInput = readInput("Day${day}_test")
        val part1 = part1(testInput)
        check(part1 == 136) { "$part1" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
