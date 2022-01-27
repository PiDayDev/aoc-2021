package y19

import java.util.*

private const val day = "07"

fun main() {
    infix fun List<Long>.executeWithSettings(settings: List<Long>): Long {
        val pipe = Stack<Long>()
        pipe.push(0)
        val processors = settings.associateWith {
            IntCodeProcessor(initialCodes = this, stopAfterOutput = true)
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

    fun solve(input: List<String>, range: LongRange): Long {
        val codes = input.first().split(",").map { it.toLong() }
        val allSettings = permutations(range.toList())
        val map = allSettings.associateWith { codes executeWithSettings it }
        return map.values.maxOf { it }
    }

    fun part1(input: List<String>) = solve(input, 0L..4L)

    fun part2(input: List<String>) = solve(input, 5L..9L)

    val input = readInput("Day${day}")
    println(part1(input))

    check(part2(readInput("Day${day}_test")) == 139629729L)

    println(part2(input))
}
