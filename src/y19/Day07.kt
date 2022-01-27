package y19

import java.util.*

private const val day = "07"

fun main() {
    infix fun List<Int>.executeWithSettings(settings: List<Int>): Int {
        val pipe = Stack<Int>()
        pipe.push(0)
        val processors = settings.associateWith {
            IntCodeProcessor5(initialCodes = this, stopAfterOutput = true)
        }
        val inputs = settings.associateWith { phase ->
            generateSequence(phase) { pipe.pop() }.iterator()
        }
        while (processors.values.any { !it.halted() }) {
            settings.forEach { phase ->
                processors[phase]!!.process(
                    input = inputs[phase]!!,
                    output = pipe::push
                )
            }
        }
        return pipe.pop()
    }

    fun part1(input: List<String>): Int {
        val codes = input.first().split(",").map { it.toInt() }
        val allSettings = permutations((0..4).toList())
        val map = allSettings.associateWith { codes executeWithSettings it }
        return map.values.maxOf { it }
    }

    fun part2(input: List<String>): Int {
        val codes = input.first().split(",").map { it.toInt() }
        val allSettings = permutations((5..9).toList())
        val map = allSettings.associateWith {
            val result = codes executeWithSettings it
            println("$it => $result")
            result
        }
        return map.values.maxOf { it }
    }

    val input = readInput("Day${day}")
    println(part1(input))

    check(part2(readInput("Day${day}_test")) == 139629729)

    println(part2(input))
}
