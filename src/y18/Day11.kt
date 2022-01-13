package y18

import kotlin.math.max

private const val input = 5535

fun main() {
    fun power(x: Int, y: Int): Int {
        val rack = 10 + x
        val power = (rack * y + input) * rack
        return (power / 100) % 10 - 5
    }

    fun power(x: Int, y: Int, size: Int) =
        (0 until size).sumOf { dx ->
            (0 until size).sumOf { dy ->
                power(x + dx, y + dy)
            }
        }

    fun part1(): Pair<Int, Int>? {
        val xy = (1..300).flatMap { x ->
            (1..300).map { y ->
                x to y
            }
        }
        return xy.maxByOrNull { power(it.first, it.second, 3) }
    }


    fun part2(): Triple<Int, Int, Int>? {
        val xy = (1..300).flatMap { x ->
            (1..300).map { y ->
                x to y
            }
        }
        val xys = xy.flatMap { (x, y) ->
            val max = 301 - max(x, y)
            (1 until max).map { Triple(x, y, it) }
        }
        return xys.maxByOrNull { (x, y, size) -> power(x, y, size) }
    }

    println(part1())
    println(part2())
}
