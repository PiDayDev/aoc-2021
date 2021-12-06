import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.sign

private const val day = "05"

data class Point(val x: Int, val y: Int) {
    operator fun rangeTo(p: Point): List<Point> {
        val dx = p.x - x
        val dy = p.y - y
        val delta = max(dx.absoluteValue, dy.absoluteValue)
        return (0..delta).map { Point(x + it * dx.sign, y + it * dy.sign) }
    }

    fun aligned(p: Point) = x == p.x || y == p.y

    override fun toString() = "($x,$y)"
}

fun main() {

    fun parse(input: List<String>): List<List<Point>> = input.map { line ->
        val (x1, y1, x2, y2) = line.split(""",| -> """.toRegex()).map { it.toInt() }
        Point(x1, y1)..Point(x2, y2)
    }

    fun countOverlaps(lines: List<List<Point>>): Int {
        val m = mutableMapOf<Point, Int>()
        lines.forEach { it.forEach { p -> m[p] = (m[p] ?: 0) + 1 } }
        return m.count { it.value > 1 }
    }

    fun part1(input: List<String>) = countOverlaps(parse(input).filter { it.first().aligned(it.last()) })

    fun part2(input: List<String>) = countOverlaps(parse(input))

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
