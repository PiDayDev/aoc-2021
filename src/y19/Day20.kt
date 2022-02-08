package y19

private const val day = 20

fun main() {
    fun tag(input: List<String>, y: Int, x: Int): String {
        val rows = input
            .drop((y - 2).coerceAtLeast(0))
            .take(5)
        val line1 = rows.map { it[x] }
        val line2 = input[y].drop((x - 2).coerceAtLeast(0)).take(5).toList()
        return (line1 + line2).filter { it in 'A'..'Z' }.joinToString("")
    }

    val input = readInput("Day${day}")

    val tags = mutableMapOf<Pair<Int, Int>, String>()
    val cells = mutableSetOf<Pair<Int, Int>>()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, cell ->
            val tag = tag(input, y, x)
            if (cell == '.') {
                val pos = x to y
                cells.add(pos)
                if (tag.length == 2) {
                    tags[pos] = tag
                }
            }
        }
    }
    val warpFriends = tags.keys
        .groupBy { tags[it]!! }.values
        .filter { it.size == 2 }
        .flatMap { (a, b) -> listOf(a to b, b to a) }
        .toMap()
    val source = tags.toList().first { it.second == "AA" }.first
    val destination = tags.toList().first { it.second == "ZZ" }.first

    fun part1(): Int {
        val distances = minDistances(source) { (x, y) ->
            listOfNotNull(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1, warpFriends[x to y])
                .filter { it in cells }
        }
        return distances[destination]!!
    }

    fun part2(): Int {
        val allWarps = warpFriends.keys + warpFriends.values
        val xMax = input.first().length
        val yMax = input.size
        val outerWarps =
            allWarps.filter { it.first <= 2 || it.first + 3 >= xMax || it.second <= 2 || it.second + 3 >= yMax }
        val innerWarps = allWarps - outerWarps.toSet()

        fun friends(triple: Triple<Int, Int, Int>): List<Triple<Int, Int, Int>> {
            val (x, y, z) = triple
            val xy = x to y
            val warpTo = warpFriends[xy]?.let { Triple(it.first, it.second, z) }
            val warpDestination = when (xy) {
                in innerWarps -> warpTo?.copy(third = z + 1)
                in outerWarps -> warpTo?.copy(third = z - 1)
                else -> null
            }
            return listOfNotNull(
                warpDestination,
                triple.copy(first = x + 1),
                triple.copy(first = x - 1),
                triple.copy(second = y + 1),
                triple.copy(second = y - 1),
            ).filter {
                it.first to it.second in cells && it.third >= 0
            }
        }

        val fromT = Triple(source.first, source.second, 0)
        val toT = Triple(destination.first, destination.second, 0)

        val distances = minDistances(fromT, toT, ::friends)
        return distances[toT]!!
    }

    println(part1())
    println(part2())
}
