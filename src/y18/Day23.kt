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

    fun part2(input: List<String>): Long = 0L

    val input = readInput("Day${day}")
    println(part1(input))

//    val t2 = part2(readInput("Day${day}_test"))
//    println(t2)
//    check(false)
    val a2 = part2(input)
    println(a2)
    check(a2 in 120000001 until 135016334)
    check(a2 !in listOf(
        133_022_408L,
        131_724_408L,
        131_724_012L,
        130_370_670L,
    ))
//130370670
}
