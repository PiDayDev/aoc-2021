private const val day = "12"

private typealias Arcs = List<Pair<String, String>>

private operator fun Arcs.get(node: String): List<String> =
    mapNotNull { (a, b) ->
        when {
            a == node -> b
            b == node -> a
            else -> null
        }
    }

private fun String.isLarge() = this == this.uppercase()

fun main() {
    fun graph(input: List<String>): Arcs = input
        .map { it.split("-") }
        .map { (a, b) -> a to b }

    fun isValidForPart1(path: List<String>): Boolean {
        val lo = path.filter { !it.isLarge() }
        return lo.size == lo.toSet().size
    }

    fun isValidForPart2(path: List<String>): Boolean {
        val lo = path.filter { !it.isLarge() }
        return lo.size - 1 <= lo.toSet().size && lo.count { it == "start" } <= 1 && lo.count { it == "end" } <= 1
    }

    fun countPaths(input: List<String>, isValidPath: (List<String>) -> Boolean): Int {
        fun appendOneNode(arcs: Arcs, prefix: List<String>) = arcs[prefix.last()]
            .map { prefix + it }
            .filter { isValidPath(it) }

        val arcs: Arcs = graph(input)
        var paths = listOf(listOf("start"))
        val finishedPaths = mutableListOf<List<String>>()
        while (paths.isNotEmpty()) {
            val (complete, incomplete) = paths
                .flatMap { appendOneNode(arcs, it) }
                .partition { it.last() == "end" }
            finishedPaths.addAll(complete)
            paths = incomplete
        }
        return finishedPaths.size
    }

    fun part1(input: List<String>) = countPaths(input, ::isValidForPart1)

    fun part2(input: List<String>) = countPaths(input, ::isValidForPart2)

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 1)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
