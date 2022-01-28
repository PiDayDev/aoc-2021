package y19

import kotlin.math.absoluteValue

private const val day = 10

private typealias Asteroid = Pair<Int, Int>

private val Asteroid.x
    get() = first
private val Asteroid.y
    get() = second

fun main() {
    fun areAligned(a: Asteroid, b: Asteroid, c: Asteroid) =
        (c.x - a.x) * (b.y - a.y) == (b.x - a.x) * (c.y - a.y)

    fun Asteroid.isBetween(a: Asteroid, b: Asteroid) =
        areAligned(a, b, this) && (b.x - x) * (x - a.x) >= 0 && (b.y - y) * (y - a.y) >= 0

    fun Asteroid.canSee(other: Asteroid, rest: List<Asteroid>) =
        (rest - other).none { it.isBetween(this, other) }

    val input = readInput("Day${day}")

    val asteroids = input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            when (c) {
                // use negative y to have a classic cartesian chart with highest y on top
                '#' -> x to -y
                else -> null
            }
        }
    }.toMutableList()

    val scores = asteroids.associateWith {
        val rest = asteroids - it
        rest.count { other -> it.canSee(other, rest) }
    }
    val best = scores.maxByOrNull { (_, v) -> v }!!
    println("PART 1: ${best.value}\n")

    val base = best.key

    println("PART 2:\nBase $base")

    val toVaporize = asteroids - base
    val firstBatch = toVaporize.filter { base.canSee(it, toVaporize) }

    fun m(it: Pair<Int, Int>): Double = (it.y - base.y).toDouble() / (it.x - base.x).toDouble()

    // divide into axes and quadrants
    val n = firstBatch.filter { it.x == base.x && it.y > base.y }
    val e = firstBatch.filter { it.x > base.x }.sortedByDescending(::m)
    val s = firstBatch.filter { it.x == base.x && it.y < base.y }
    val w = firstBatch.filter { it.x < base.x }.sortedByDescending(::m)

    println(listOf(n, e, s, w).flatten()[199].let { it.x.absoluteValue * 100 + it.y.absoluteValue })
}
