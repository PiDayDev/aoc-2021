package y17

import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.math.sqrt

private const val input = 277678


fun main() {

    fun Map<Int, Pair<Int, Int>>.interpolate(n: Int): Pair<Int, Int> {
        val (a, b) = keys.sorted().windowed(2).first { n in it[0]..it[1] }
        val (xa, ya) = get(a)!!
        val (xb, yb) = get(b)!!
        val d = n - a
        return (xa + d * (xb - xa).sign) to (ya + d * (yb - ya).sign)
    }

    fun part1(): Int {
        val squareRoot = sqrt(input.toDouble()).toInt()
        val maxOdd = squareRoot - (1 - squareRoot % 2)
        val maxOddSquare = maxOdd * maxOdd
        val positions = mutableMapOf<Int, Pair<Int, Int>>()
        val side = maxOdd + 2
        var n = maxOddSquare
        var x = maxOdd / 2
        var y = maxOdd / 2
        positions[n] = x to y
        n += 1
        x += 1
        positions[n] = x to y
        n += side - 2
        y -= side - 2
        positions[n] = x to y
        n += side - 1
        x -= side - 1
        positions[n] = x to y
        n += side - 1
        y += side - 1
        positions[n] = x to y
        n += side - 1
        x += side - 1
        positions[n] = x to y
        val (xx, yy) = positions.interpolate(input)
        return xx.absoluteValue + yy.absoluteValue
    }

    fun Pair<Int, Int>.next(): Pair<Int, Int> {
        val (x, y) = this
        return when {
            x >= 0 && y == x -> x + 1 to y
            x < 0 && y == x -> x to y + 1
            x > 0 && y == -x -> x - 1 to y
            x < 0 && y == -x -> x + 1 to y
            x > 0 && y in -x..x -> x to y - 1
            x < 0 && y in x..-x -> x to y + 1
            y > 0 && x in -y..y -> x + 1 to y
            y < 0 && x in y..-y -> x - 1 to y
            else -> throw IllegalStateException(toString())
        }
    }

    fun part2(): Int {
        val start = 0 to 0
        val grid = mutableMapOf(start to 1)
        return generateSequence(start.next()) { it.next() }
            .map { pos ->
                val (x, y) = pos
                val value = listOf(
                    x - 1 to y - 1, x - 1 to y, x - 1 to y + 1, x to y - 1,
                    x to y + 1, x + 1 to y - 1, x + 1 to y, x + 1 to y + 1,
                ).sumOf { grid[it] ?: 0 }
                grid[pos] = value
                value
            }
            .first { it > input }
    }

    println(part1())
    println(part2())
}
