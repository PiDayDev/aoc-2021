package y16

private const val day = "04"

fun main() {
    fun String.isValid(): Boolean {
        val name = substringBefore("[").replace("""[-0-9]""".toRegex(), "")
        val frequencies = name
            .groupBy { it }
            .mapValues { (_, v) -> v.size }
            .toList()
            .sortedWith(compareBy({ -it.second }, { it.first }))
        val checksum = frequencies.joinToString("") { "${it.first}" }
        val expected = substringAfter("[").substringBefore("]")
        return expected == checksum.take(expected.length)
    }

    fun String.sector() = substringBefore("[").substringAfterLast("-").toInt()

    fun String.rot(n: Int) = toList().map { if (it == '-') ' ' else 'a' + (it.code - 'a'.code + n) % 26 }.joinToString("") { "$it" }

    fun part1(input: List<String>) = input.filter { it.isValid() }.sumOf { it.sector() }

    fun part2(input: List<String>) = input
        .filter { it.isValid() }
        .firstNotNullOf { room ->
            val sector = room.sector()
            val name = room.substringBeforeLast("-").rot(sector)
            if ("north" in name) sector else null
        }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
