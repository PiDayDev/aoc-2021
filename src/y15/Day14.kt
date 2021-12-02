package y15

import kotlin.math.min

private const val day = "14"

private data class Reindeer(val speed: Int, val resistance: Int, val sleep: Int) {
    fun race(seconds: Int): Int {
        val cycle = resistance + sleep
        val fullCycles = seconds / cycle
        val residual = seconds % cycle
        return fullCycles * speed * resistance + min(residual, resistance) * speed
    }
}

fun main() {
    val rowRegex =
        Regex("""\w+ can fly ([0-9]+) km/s for ([0-9]+) seconds, but then must rest for ([0-9]+) seconds""")

    fun parse(input: List<String>) = input.map { row ->
        val (speed, resistance, sleep) = rowRegex.find(row)!!.destructured
        Reindeer(speed.toInt(), resistance.toInt(), sleep.toInt())
    }

    fun part1(input: List<String>) = parse(input).maxOf { it.race(2503) }

    fun part2(input: List<String>): Int {
        val deerList = parse(input)
        val scores = deerList.associateWith { 0 }.toMutableMap()
        (1..2503).forEach { s ->
            val partials = deerList.associateWith { it.race(s) }
            val max = partials.maxOf { it.value }
            partials.filter { it.value == max }.forEach { (d, _) -> scores[d] = 1 + scores[d]!! }
        }
        return scores.maxOf { it.value }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
