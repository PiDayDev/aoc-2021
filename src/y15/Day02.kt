package y15

private const val day = "02"

fun main() {
    fun part1(input: List<String>) = input.sumOf { row ->
        val (a, b, c) = row.split("x").map { it.toInt() }.sorted()
        3 * a * b + 2 * a * c + 2 * b * c
    }

    fun part2(input: List<String>) = input.sumOf { row ->
        val (a, b, c) = row.split("x").map { it.toInt() }.sorted()
        ( a + b )* 2 + a * b * c
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
