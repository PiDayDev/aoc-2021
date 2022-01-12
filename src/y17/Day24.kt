package y17

private const val day = 24

private typealias Part = Pair<Int, Int>

fun main() {
    fun List<String>.toParts(): List<Part> =
        map { it.split("/").map { e -> e.toInt() }.sorted() }.map { (a, b) -> a to b }

    operator fun Part.contains(n: Int) = first == n || second == n

    operator fun Part.minus(n: Int) = when (n) {
        first -> second
        second -> first
        else -> throw IllegalArgumentException("$n not in $this")
    }

    fun List<Part>.strength() = sumOf { (a, b) -> a + b }

    fun bestPath(
        startingPort: Int,
        usableParts: Collection<Part>,
        pathSoFar: List<Part>,
        criterion: (List<Part>) -> Int
    ): List<Part> {
        val candidates = usableParts.filter { startingPort in it }
        return if (candidates.isEmpty()) {
            pathSoFar
        } else {
            candidates
                .map { bestPath(it - startingPort, usableParts - it, pathSoFar + it, criterion) }
                .maxByOrNull(criterion)
                ?: emptyList()
        }
    }

    fun part1(parts: List<Part>) =
        bestPath(0, parts, emptyList()) { it.strength() }.strength()

    fun part2(parts: List<Part>) =
        bestPath(0, parts, emptyList()) { it.size * 10000 + it.strength() }.strength()

    val input = readInput("Day${day}")
    val parts = input.toParts()
    println(part1(parts))
    println(part2(parts))
}
