package y20

import y20.Direction12.E
import kotlin.math.absoluteValue

private const val day = "12"

private enum class Direction12(val n: Int, val w: Int) {
    N(1, 0), E(0, -1), S(-1, 0), W(0, 1);

    fun turnRight() = values()[(ordinal + 1) % values().size]
}

fun main() {

    fun part1(input: List<String>): Int {
        var n = 0
        var w = 0
        var dir = E
        input.forEach {
            val amount = it.drop(1).toInt()
            when (it.take(1)) {
                "N" -> n += amount
                "S" -> n -= amount
                "W" -> w += amount
                "E" -> w -= amount
                "R" -> repeat(amount / 90) { dir = dir.turnRight() }
                "L" -> repeat(4 - amount / 90) { dir = dir.turnRight() }
                "F" -> {
                    n += amount * dir.n
                    w += amount * dir.w
                }
            }
        }
        return n.absoluteValue + w.absoluteValue
    }

    fun part2(input: List<String>): Int {
        var waypointN = 1
        var waypointW = -10
        var n = 0
        var w = 0
        input.forEach {
            val amount = it.drop(1).toInt()
            fun turnWaypointRight() {
                val n1 = waypointW
                val w1 = -waypointN
                waypointN = n1
                waypointW = w1
            }
            when (it.take(1)) {
                "N" -> waypointN += amount
                "S" -> waypointN -= amount
                "W" -> waypointW += amount
                "E" -> waypointW -= amount
                "R" -> repeat(amount / 90) { turnWaypointRight() }
                "L" -> repeat(4 - amount / 90) { turnWaypointRight() }
                "F" -> {
                    n += amount * waypointN
                    w += amount * waypointW
                }
            }
        }
        return n.absoluteValue + w.absoluteValue
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 962)
    val p2 = part2(input)
    println(p2)
    check(p2 == 56135)
}
