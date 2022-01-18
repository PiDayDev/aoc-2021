package y18

import kotlin.math.max

private const val day = "17"

private const val CLAY = '#'
private const val SAND = '.'
private const val WATER = 'l'
private const val LAKE = '~'
private const val NA = 'X'
private const val SPRING = '+'

fun main() {
    fun String.toRange(): IntRange {
        val (start, end) = "${substringAfter("=")}..-1".split("..").map { it.toInt() }
        return start..max(start, end)
    }

    fun String.toXY(): Pair<IntRange, IntRange> {
        val (xs, ys) = split(", ").sorted().map { it.toRange() }
        return xs to ys
    }

    fun List<MutableMap<Int, Char>>.row(xy: Pair<Int, Int>) =
        getOrNull(xy.second)

    fun List<MutableMap<Int, Char>>.at(xy: Pair<Int, Int>) =
        row(xy)?.getOrDefault(xy.first, NA) ?: NA

    fun List<MutableMap<Int, Char>>.set(xy: Pair<Int, Int>, c: Char) =
        row(xy)?.put(xy.first, c)

    fun List<MutableMap<Int, Char>>.spill(x: Int, y: Int): List<Pair<Int, Int>> {
        val out = mutableListOf<Pair<Int, Int>>()
        val down = x to y + 1
        when (at(down)) {
            SAND -> {
                set(down, WATER)
                out += down
            }
            LAKE, CLAY -> {
                val sides = listOf(x - 1 to y, x + 1 to y)
                sides.forEach { side ->
                    if (at(side) == SAND) {
                        set(side, WATER)
                        out += side
                    }
                }
            }
        }
        return out
    }

    fun List<MutableMap<Int, Char>>.spill(xy: Pair<Int, Int>) = spill(xy.first, xy.second)

    fun MutableMap<Int, Char>.flood(): List<IntRange> {
        val delta = asSequence().first().key
        val row = values.joinToString("")
        val lakes = """$CLAY$WATER+$CLAY""".toRegex().findAll(row)
            .flatMap { mr -> mr.groups }
            .mapNotNull { it?.range }
            .map { delta + it.first + 1 until delta + it.last }
            .toList()
        lakes.flatten().forEach { this[it] = LAKE }
        return lakes
    }

    fun List<MutableMap<Int, Char>>.flood(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        forEachIndexed { y, row ->
            val ranges = row.flood()
            val up = y - 1
            val waters = ranges.flatMap { r -> r.filter { at(it to up) == WATER } }
            result.addAll(waters.map { it to up })
        }
        return result
    }

    fun List<IntRange>.joined() =
        flatMap { listOf(it.first, it.last) }.sorted().let { it.first()..it.last() }

    fun simulate(input: List<String>): List<Map<Int, Char>> {
        val ranges = input.map { it.toXY() }
        val xRange = ranges.map { it.first }.joined()
        val yRange = ranges.map { it.second }.joined()
        val grid = (0..yRange.last).map { (xRange.first - 1..xRange.last + 1).associateWith { SAND }.toMutableMap() }
        ranges.forEach { (xs, ys) ->
            ys.forEach { y ->
                val row = grid[y]
                xs.forEach { x ->
                    row[x] = CLAY
                }
            }
        }
        grid[0][500] = SPRING

        var springs = listOf(500 to 0)
        while (springs.isNotEmpty()) {
            while (springs.isNotEmpty()) {
                springs = springs.flatMap { grid.spill(it) }
            }
            springs = grid.flood()
        }

//        grid.forEach { println(it.values.joinToString("")) }

        return grid.filterIndexed { y, _ -> y in yRange }
    }

    fun part1(grid: List<Map<Int, Char>>) = grid
        .sumOf { row -> row.values.count { it == WATER || it == LAKE } }

    fun part2(grid: List<Map<Int, Char>>) = grid
        .sumOf { row -> row.values.count { it == LAKE } }

    try {
        val testInput = readInput("Day${day}_test")
        val test1 = part1(simulate(testInput))
        check(test1 == 57) { "$test1" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    val grid = simulate(input)
    println(part1(grid))
    println(part2(grid))
}
