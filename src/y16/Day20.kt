package y16

import kotlin.math.max

private const val day = "20"

private const val MAX = 4_294_967_295L

private infix fun LongRange.mergeWith(r: LongRange): List<LongRange> =
    when {
        r.first < first -> r.mergeWith(this)
        r.first > last + 1L -> listOf(this, r)
        else -> listOf(first..max(r.last, last))
    }

private fun List<LongRange>.merge() =
    drop(1).fold(take(1)) { acc: List<LongRange>, curr: LongRange ->
        acc.flatMap { it mergeWith curr }.distinct()
    }

fun main() {

    fun validIps(input: List<String>): List<Long> {
        val ranges = input
            .map { range ->
                val (lo, hi) = range.split("-").map { it.toLong() }
                lo..hi
            }
            .sortedBy { it.first }

        val merged = generateSequence(ranges) { it.merge() }
            .zipWithNext()
            .first { it.second.size == it.first.size }
            .second
            .sortedBy { it.first }

        val complement = merged
            .mapIndexedNotNull { index, range ->
                if (range.last >= MAX) null
                else range.last + 1 until merged[index + 1].first
            }

        return complement.flatMap { it.toList() }
    }

    val input = readInput("Day${day}")

    val ips = validIps(input)
    println(ips.first())
    println(ips.size)
}
