package y19

private const val day = 24

fun main() {
    fun List<String>.gameOfBugs(): List<String> =
        mapIndexed { r, row ->
            row.mapIndexed { c, bug ->
                val bugs = listOfNotNull(
                    getOrNull(r)?.getOrNull(c + 1),
                    getOrNull(r)?.getOrNull(c - 1),
                    getOrNull(r + 1)?.getOrNull(c),
                    getOrNull(r - 1)?.getOrNull(c),
                ).count { it == '#' }
                when {
                    bug == '#' && bugs == 1 -> '#'
                    bug == '.' && bugs in 1..2 -> '#'
                    else -> '.'
                }
            }.joinToString("")
        }

    fun List<String>.biodiversity() =
        flatMap { it.toList() }.mapIndexed { index, c -> if (c == '#') 1 shl index else 0 }.sum()

    fun part1(input: List<String>) = generateSequence(input) { it.gameOfBugs() }
        .scan(listOf<List<String>>()) { acc, v -> acc + listOf(v) }
        .first { it.distinct().size < it.size }
        .last()
        .biodiversity()

    fun part2(input: List<String>): Int {
        return input.size
    }

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
