package y17

import kotlin.math.min

private const val day = "11"

fun main() {

    fun distance(steps: Map<String, Int>): Int {
        var s = steps["s"] ?: 0
        var se = steps["se"] ?: 0
        var sw = steps["sw"] ?: 0
        var n = steps["n"] ?: 0
        var ne = steps["ne"] ?: 0
        var nw = steps["nw"] ?: 0
        val senw = min(se, nw)
        se -= senw
        nw -= senw
        val nesw = min(sw, ne)
        sw -= nesw
        ne -= nesw
        val nmin = min(min(ne, nw), n)
        n -= nmin
        nw -= nmin
        ne -= nmin
        val smin = min(min(se, sw), s)
        s -= smin
        sw -= smin
        se -= smin
        val sn = min(s, n)
        s -= sn
        n -= sn
        val a = min(s, nw)
        s -= a
        nw -= a
        sw += a
        val b = min(s, ne)
        s -= b
        ne -= b
        se += b
        val c = min(n, sw)
        n -= c
        sw -= c
        nw += c
        val d = min(n, se)
        n -= d
        se -= d
        ne += d
        return s + se + sw + n + ne + nw
    }

    fun part1(input: List<String>): Int {
        val steps = input
            .first()
            .split(",")
            .groupBy { it }
            .mapValues { (_, v) -> v.size }
        return distance(steps)
    }


    fun part2(input: List<String>): Int {
        val steps = input
            .first()
            .split(",")
        var max = 0
        steps.fold(emptyList<String>()) { partial, foo ->
            val acc = partial + foo
            val dist = distance(acc.groupBy { it }.mapValues { (_, v) -> v.size })
            if (dist > max) max = dist
            acc
        }
        return max
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
