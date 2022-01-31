package y19

import kotlin.math.absoluteValue

private const val day = 16

fun main() {
    fun pattern(n: Int) = listOf(0, 1, 0, -1).flatMap { p -> List(n) { p } }

    fun pattern(n: Int, size: Int) = List(size / n + 2) { pattern(n) }.flatten().drop(1).take(size)

    fun List<Int>.step(): List<Int> =
        List(size) { index ->
            zip(pattern(index + 1, size)) { a, b -> a * b }.sum().absoluteValue % 10
        }

    fun part1(input: List<String>): Int {
        val digits = input.joinToString("").split("").filter { it.isNotBlank() }.map { it.toInt() }
        val result = (1..100).fold(digits) { d, _ -> d.step() }
        return result.take(8).joinToString("").toInt()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    try {
        val testInput = listOf("80871224585914546619083218645595")
        check(part1(testInput) == 24176176)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
