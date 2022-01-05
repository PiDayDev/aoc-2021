package y17

import kotlin.math.max

private const val day = "08"

private fun String.exec(registers: MutableMap<String, Int>) {
    val tokens = split(" ")
    val (target, op, amount) = tokens
    val (source, cmp, value) = tokens.takeLast(3)

    val srcValue = registers[source] ?: 0
    val cmpValue = value.toInt()
    val proceed = when (cmp) {
        ">=" -> srcValue >= cmpValue
        ">" -> srcValue > cmpValue
        "<=" -> srcValue <= cmpValue
        "<" -> srcValue < cmpValue
        "!=" -> srcValue != cmpValue
        else -> srcValue == cmpValue
    }
    if (proceed) {
        val mult = if (op == "inc") +1 else -1
        registers[target] = (registers[target] ?: 0) + mult * amount.toInt()
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val registers = mutableMapOf<String, Int>()
        input.forEach { it.exec(registers) }
        return registers.values.maxOf { it }
    }

    fun part2(input: List<String>): Int {
        var maxV = 0
        val registers = mutableMapOf<String, Int>()
        input.forEach {
            it.exec(registers)
            maxV = max(maxV, registers.values.maxOfOrNull { v -> v } ?: 0)
        }
        return maxV
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
