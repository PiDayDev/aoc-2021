package y20

private const val day = "09"

fun main() {
    fun List<Long>.sums() = flatMapIndexed { i, a ->
        (i + 1 until size).map { get(it) + a }
    }

    fun part1(input: List<String>) = input
        .map { it.toLong() }
        .windowed(26)
        .first { it.last() !in it.dropLast(1).sums() }
        .last()

    fun part2(input: List<String>, goal: Long): Long {
        val candidates = input
            .map { it.toLong() }
            .filter { it < goal }
        return candidates
            .indices
            .firstNotNullOf { index ->
                val tail = candidates.take(index + 1).reversed()
                val sums = tail.scan(0L) { a, b -> a + b }
                val pos = sums.indexOf(goal)
                if (pos >= 0) {
                    tail.take(pos).sorted().let { it.first() + it.last() }
                } else null
            }
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 1930745883L)
    val p2 = part2(input, p1)
    println(p2)
    check(p2 == 268878261L)
}
