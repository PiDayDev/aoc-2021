package y19

import java.util.concurrent.atomic.AtomicInteger

private const val day = "07"

fun main() {
    infix fun List<Int>.withSettings(settings: List<Int>): Int {
        val pipe = AtomicInteger(0)
        val processor = IntCodeProcessor5()
        settings.forEach { phase ->
            processor.process(this, generateSequence(phase) { pipe.get() }.iterator()) { pipe.set(it) }
        }
        return pipe.get()
    }

    fun part1(input: List<String>): Int {
        val codes = input.first().split(",").map { it.toInt() }
        val allSettings = permutations((0..4).toList())
        val map = allSettings.associateWith { codes withSettings it }
        map.forEach(::println)
        return map.values.maxOf { it }
    }

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
