package y18

private const val day = "07"

fun main() {
    fun List<String>.asDAG(): Pair<List<Pair<String, String>>, MutableList<String>> {
        val arcs = map { line -> line.split(" ").let { it[1] to it[7] } }
        val nodes = arcs.flatMap { it.toList() }.distinct().toMutableList()
        return arcs to nodes
    }

    fun part1(input: List<String>): String {
        val (arcs, nodes) = input.asDAG()
        val result = mutableListOf<String>()
        while (nodes.isNotEmpty()) {
            val candidates = nodes.filter { candidate ->
                arcs.none { it.second == candidate && it.first !in result }
            }
            val winner = candidates.minOf { it }
            nodes -= winner
            result += winner
        }

        return result.joinToString("")
    }

    fun String.weight() = get(0).code - 'A'.code + 61

    fun part2(input: List<String>): Int {
        // IF the number of workers is enough to parallelize all available tasks,
        //  I just have to found the critical path (= the longest path on the DAG)
        val (arcs, nodes) = input.asDAG()
        val cache = mutableMapOf<String, Int>()

        fun longestPath(from: String): Int =
            when (val memo = cache[from]) {
                null -> {
                    val rest = arcs.filter { it.first == from }.map { it.second }.maxOfOrNull { longestPath(it) } ?: 0
                    val length = from.weight() + rest
                    cache[from] = length
                    length
                }
                else -> memo
            }

        val starts = nodes.filter { candidate -> arcs.none { it.second == candidate } }
        val criticalPaths = starts.associateWith { longestPath(it) }
        return criticalPaths.maxOf { it.value }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
