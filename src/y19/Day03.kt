package y19

import kotlin.math.absoluteValue
import kotlin.math.sign

private const val day = "03"

private data class Point03(val x: Int, val y: Int, val cost: Int) {
    fun interpolate(p: Point03): List<Point03> {
        val dx = (p.x - x).sign
        val dy = (p.y - y).sign
        val dc = (p.cost - cost).sign
        val dist = (p.x - x).absoluteValue + (p.y - y).absoluteValue
        return (0..dist).map {
            Point03(x + it * dx, y + it * dy, cost + it * dc)
        }
    }
}

private fun String.wireWithCosts(): List<Point03> {
    val vertices = split(",").scan(Point03(0, 0, 0)) { point, movement ->
        val direction = movement.take(1)
        val distance = movement.drop(1).toInt()
        val (x, y) = point
        when (direction) {
            "U" -> Point03(x, y + distance, point.cost + distance)
            "D" -> Point03(x, y - distance, point.cost + distance)
            "L" -> Point03(x - distance, y, point.cost + distance)
            "R" -> Point03(x + distance, y, point.cost + distance)
            else -> point
        }
    }
    return vertices
        .zipWithNext { p, q -> p.interpolate(q) }
        .flatten()
        .distinct()
}

fun main() {

    fun List<Point03>.positions() =
        filterNot { it.x == 0 && it.y == 0 }.map { it.x to it.y }

    fun part1(input: List<String>): Int {
        val (p1, p2) = input.map { it.wireWithCosts().positions() }
        return p1.intersect(p2.toSet()).minOf { (x, y) ->
            x.absoluteValue + y.absoluteValue
        }
    }

    fun part2(input: List<String>): Int {
        val (w1, w2) = input.map { it.wireWithCosts() }
        val p1 = w1.positions()
        val p2 = w2.positions()
        val crossings = p1.intersect(p2.toSet())
        return crossings.map { (x, y) ->
            val z1 = w1.filter { it.x == x && it.y == y }
            val z2 = w2.filter { it.x == x && it.y == y }
            val cost = z1.minOf { it.cost } + z2.minOf { it.cost }
            cost
        }
            .minOf { it }
    }

    try {
        val testInput = readInput("Day${day}_test")
        val p2 = part2(testInput)
        check(p2 == 30) { "$p2" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
