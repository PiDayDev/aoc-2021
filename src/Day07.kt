import kotlin.math.absoluteValue

private const val day = "07"

fun main() {
    fun parseCrabs(input: List<String>): Pair<List<Int>, IntRange> {
        val crabs = input.last().split(",").map { it.toInt() }.sorted()
        val range = crabs.minOf { it }..crabs.maxOf { it }
        return crabs to range
    }

    fun part1(input: List<String>): Int {
        val (crabs, range) = parseCrabs(input)
        return range.minOf { crabs.sumOf { c -> (c - it).absoluteValue } }
    }

    fun part2(input: List<String>): Int {
        val (crabs, range) = parseCrabs(input)
        return range.minOf {
            crabs.sumOf { c ->
                val d = (c - it).absoluteValue
                d * (d + 1) / 2
            }
        }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
