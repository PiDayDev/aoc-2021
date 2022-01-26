package y18

import kotlin.math.absoluteValue

private const val day = 25

class DisjointSet<I>(initialSet: List<I>) {
    private val ancestors: MutableMap<I, I> =
        initialSet.associateWith { it }.toMutableMap()

    fun findPartition(elem: I): I =
        when (val ancestor = ancestors[elem]!!) {
            elem -> elem
            else -> findPartition(ancestor)
        }

    fun merge(a: I, b: I) {
        val ancestor1 = findPartition(a)
        val ancestor2 = findPartition(b)
        ancestors[ancestor1] = ancestor2
        ancestors[a] = ancestor2
    }

}

data class Point25(val coordinates: List<Int>) {
    fun distance(p: Point25) = coordinates.zip(p.coordinates) { a, b -> a - b }.sumOf { it.absoluteValue }
}

fun main() {

    fun part1(input: List<String>): Int {
        val points = input.map { row -> row.split(",").map { it.toInt() } }.map { Point25(it) }
        val constellations = DisjointSet(points)
        points.forEach { p ->
            points.forEach { q ->
                if (p.distance(q) <= 3) {
                    constellations.merge(p, q)
                }
            }
        }
        return points.map { constellations.findPartition(it) }.distinct().size
    }

    val input = readInput("Day${day}")
    println(part1(input))
}
