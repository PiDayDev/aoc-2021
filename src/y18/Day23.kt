package y18

import kotlin.math.absoluteValue

private const val day = 23

private data class Bot(val x: Long, val y: Long, val z: Long, val range: Long = 0L) {
    infix fun reaches(b: Bot) = distance(b) <= range

    val dist by lazy { distance(Bot(0, 0, 0)) }

    fun distance(b: Bot): Long =
        (x - b.x).absoluteValue + (y - b.y).absoluteValue + (z - b.z).absoluteValue

    fun star() = listOf(
        this,
        copy(x = x - range), copy(x = x + range),
        copy(y = y - range), copy(y = y + range),
        copy(z = z - range), copy(z = z + range),
    )

}

private fun String.toBot(): Bot {
    val (x, y, z) = substringAfter("<").substringBefore(">").split(",").map { it.toLong() }
    val r = substringAfter("r=").toLong()
    return Bot(x, y, z, r)
}

fun main() {
    fun part1(input: List<String>): Int {
        val bots = input.map { it.toBot() }
        val strongest = bots.maxByOrNull { it.range }!!
        return bots.count { strongest reaches it }
    }

    /**
     *  Over-simplified version with 3-D coordinates flattened to just the distance.
     *  Luckily... it worked.
     */
    fun part2(input: List<String>): Long {
        val bots = input.map { it.toBot() }
        val flat = bots
            .map { it.star().map { s -> s.dist }.sorted() }
            .map { it.first()..it.last() }
        val pointsWithDistances = flat
            .flatMap { listOf(it.first, it.last) }
            .distinct()
            .associateWith { here -> flat.count { here in it } }
        val maxBotCount = pointsWithDistances.values.maxOf { it }
        val bestBot = pointsWithDistances.filterValues { it == maxBotCount }.toList()

        return bestBot.first().first
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
