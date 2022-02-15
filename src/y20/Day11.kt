package y20

import kotlin.math.absoluteValue
import kotlin.math.sign

private const val day = "11"

private typealias Pos = Pair<Int, Int>

operator fun Pos.minus(p: Pos) = (first - p.first) to (second - p.second)

fun main() {
    fun List<String>.toSeats(): Map<Pos, Boolean> =
        flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c == 'L') x to y else null
            }
        }.associateWith { true }

    val input = readInput("Day$day")
    val seats = input.toSeats()
    val locations = seats.keys

    val neighborsCache = mutableMapOf<Pos, List<Pos>>()

    fun immediateNeighborsOf(s: Pos): List<Pos> {
        val result = neighborsCache[s] ?: locations.filter {
            val dx = it.first - s.first
            val dy = it.second - s.second
            dx in -1..1 && dy in -1..1 && (dx != 0 || dy != 0)
        }
        return result.also { neighborsCache[s] = it }
    }

    val visibleCache = mutableMapOf<Pos, List<Pos>>()

    fun visibleFrom(s: Pos): List<Pos> {
        val result = visibleCache[s] ?: locations
            .filter {
                val (dx, dy) = it - s
                (dx != 0 || dy != 0) && (dx * dy == 0 || (dx / dy).absoluteValue == (dy / dx).absoluteValue)
            }
            .groupBy { (it - s).toList().map { v -> v.sign } }
            .values
            .mapNotNull { line ->
                line.minByOrNull { p -> (p - s).let { (dx, dy) -> dx.absoluteValue + dy.absoluteValue } }
            }
        return result.also { visibleCache[s] = it }
    }

    fun Map<Pos, Boolean>.step(threshold: Int, observed: (Pos) -> List<Pos>) =
        mapValues { (pos, free) ->
            val occupied = observed(pos).count { !getOrDefault(it, true) }
            when {
                free && occupied == 0 -> false
                !free && occupied >= threshold -> true
                else -> free
            }
        }

    fun gameOfSeats(threshold: Int, observed: (Pos) -> List<Pos>) =
        generateSequence(seats) { it.step(threshold, observed) }
            .zipWithNext()
            .first { (a, b) -> a == b }
            .second
            .count { !it.value }

    fun part1(): Int = gameOfSeats(4, ::immediateNeighborsOf)

    fun part2(): Int = gameOfSeats(5, ::visibleFrom)

    val p1 = part1()
    println(p1)
    check(p1 == 2368)
    val p2 = part2()
    println(p2)
    check(p2 == 2124)
}
