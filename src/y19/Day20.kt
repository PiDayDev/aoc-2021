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

    fun part1(input: List<String>): Int {
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
        val distances = minDistances(source) { (x, y) ->
            listOfNotNull(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1, warpFriends[x to y])
                .filter { it in cells }
        }

        return distances[destination]!!
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 1)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
