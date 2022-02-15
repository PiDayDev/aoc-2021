package y20

private const val day = "05"

fun main() {
    fun String.toId() = this
        .replace('F', '0')
        .replace('B', '1')
        .replace('L', '0')
        .replace('R', '1')
        .toInt(2)

    fun part1(input: List<String>) = input.maxOf { it.toId() }

    fun part2(input: List<String>) = input
        .map { it.toId() }
        .sorted()
        .zipWithNext()
        .first { (a, b) -> b - a > 1 }
        .let { (a, b) -> (a + b) / 2 }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 947)
    val p2 = part2(input)
    println(p2)
    check(p2 == 636)
}
