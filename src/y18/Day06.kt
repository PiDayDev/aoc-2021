package y18

import kotlin.math.absoluteValue

private const val day = "06"

fun main() {
    fun String.toPair() =
        split(", ").map { c -> c.toInt() }.let { (a, b) -> a to b }

    fun List<String>.toPairs() =
        map { it.toPair() }

    fun Pair<Int, Int>.distance(x: Int, y: Int) =
        (first - x).absoluteValue + (second - y).absoluteValue

    fun List<Pair<Int, Int>>.closest(x: Int, y: Int): Pair<Int, Int>? {
        val d = minOf { it.distance(x, y) }
        val result = filter { it.distance(x, y) == d }
        return if (result.size == 1) result[0] else null
    }

    fun List<Pair<Int, Int>>.assignAll(
        regionSizes: MutableMap<Pair<Int, Int>, Int>,
        xs: Iterable<Int>, ys: Iterable<Int>
    ) {
        xs.forEach { x ->
            ys.forEach { y ->
                closest(x, y)?.let { owner ->
                    regionSizes[owner] = regionSizes.getOrDefault(owner, 0) + 1
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val pairs = input.toPairs()
        val regionSizes = mutableMapOf<Pair<Int, Int>, Int>()
        val range = -1000..1000
        pairs.assignAll(regionSizes, range, range)

        // check for regions still growing (infinite)
        val growing = mutableMapOf<Pair<Int, Int>, Int>().apply { putAll(regionSizes) }
        val size = 3000
        pairs.assignAll(growing, -size..size, listOf(-size, size))
        pairs.assignAll(growing, listOf(-size, size), -size..size)

        val infinity = (growing.toList() - regionSizes.toList().toSet()).map { (k, _) -> k }

        return regionSizes.filter { (k, _) -> k !in infinity }.maxOf { (_, v) -> v }
    }

    fun part2(input: List<String>): Int {
        val max = 10000
        val pairs = input.toPairs()
        val range = -2 * max..+2 * max
        val xs = range.map { x -> pairs.sumOf { (it.first - x).absoluteValue } }.filter { it < max }
        val ys = range.map { y -> pairs.sumOf { (it.second - y).absoluteValue } }.filter { it < max }
        return xs.sumOf { dx ->
            ys.count { dy ->
                dx + dy < max
            }
        }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
