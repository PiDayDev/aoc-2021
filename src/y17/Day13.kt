package y17

private const val day = 13

private fun String.toDepthAndRange() = split(": ").map { it.toInt() }.let { (a, b) -> a to b }

fun main() {

    fun part1(input: List<String>) = input
        .map { it.toDepthAndRange() }
        .filter { (d, r) -> d % ((r - 1) * 2) == 0 }
        .sumOf { it.first * it.second }

    fun part2(input: List<String>): Int {
        val scanners = input.map { it.toDepthAndRange() }
        return generateSequence(1) { it + 1 }
            .first { delay -> scanners.none { (d, r) -> (delay + d) % ((r - 1) * 2) == 0 } }

    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
