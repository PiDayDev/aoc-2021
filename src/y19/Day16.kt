package y19

import kotlin.math.absoluteValue

private const val day = 16

fun main() {
    fun factor(patternLength: Int, index: Int) =
        when ((index + 1) % (patternLength * 4) / patternLength) {
            1 -> +1
            3 -> -1
            else -> 0
        }

    fun List<Int>.step(offset: Int = 0): List<Int> =
        if (offset <= size / 2) {
            List(size) { index ->
                val sum = foldIndexed(0L) { j, sum, n -> sum + n * factor(1 + index + offset, j + offset) }
                (sum.absoluteValue % 10).toInt()
            }
        } else {
            reversed().scan(0) { a, b -> (a + b) % 10 }.drop(1).reversed()
        }

    fun part1(input: List<String>): String {
        val digits = input.joinToString("").split("").filter { it.isNotBlank() }.map { it.toInt() }
        val result = (1..100).fold(digits) { d, _ -> d.step() }
        return result.take(8).joinToString("")
    }

    fun part2(input: List<String>): String {
        val digits1 = input.joinToString("").split("").filter { it.isNotBlank() }.map { it.toInt() }
        val skip = null ?: digits1.take(7).joinToString("").toInt()
        val digits = (1..10000).flatMap { digits1 }.drop(skip)
        val result = (1..100).fold(digits) { d, _ -> d.step(skip) }
        return result.take(8).joinToString("")
    }

    val testInput = listOf("80871224585914546619083218645595")
    check(part1(testInput) == "24176176")

    val testInput2 = listOf("03036732577212944063491565474664")
    check(part2(testInput2) == "84462026")

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
