package y19

private const val day = "01"

fun main() {
    fun part1(input: List<String>) = input.sumOf { it.toInt() / 3 - 2 }

    fun Int.recursiveFuel(): Int =
        this + when (val delta = (this / 3 - 2).coerceAtLeast(0)) {
            0 -> 0
            else -> delta.recursiveFuel()
        }

    fun part2(input: List<String>) = input.map { it.toInt() }.sumOf { it.recursiveFuel() - it }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
