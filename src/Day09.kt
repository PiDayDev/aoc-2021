import java.io.FileNotFoundException

private const val day = "09"
private const val BIG = 999
fun main() {

    fun List<List<Int>>.isLow(r: Int, c: Int, value: Int) = listOf(
        getOrNull(r)?.getOrNull(c - 1) ?: BIG,
        getOrNull(r)?.getOrNull(c + 1) ?: BIG,
        getOrNull(r - 1)?.getOrNull(c) ?: BIG,
        getOrNull(r + 1)?.getOrNull(c) ?: BIG,
    ).all { it > value }

    fun toHeights(input: List<String>) = input.map { r ->
        r.split("").filter { it.isNotBlank() }.map { it.toInt() }.toMutableList()
    }.toMutableList()

    fun part1(input: List<String>): Int {
        var sum = 0
        toHeights(input).forEachIndexed { r, row ->
            row.forEachIndexed { c, v ->
                if (toHeights(input).isLow(r, c, v))
                    sum += 1 + v
            }
        }
        return sum
    }

    fun MutableList<MutableList<Int>>.floodFill(r: Int, c: Int, v: Int): Int {
        val row = this[r]
        val value = row[c]
        if (value == v) return 0
        var n = 1
        this[r][c] = v
        if (r - 1 in indices) n += floodFill(r - 1, c, v)
        if (r + 1 in indices) n += floodFill(r + 1, c, v)
        if (c - 1 in row.indices) n += floodFill(r, c - 1, v)
        if (c + 1 in row.indices) n += floodFill(r, c + 1, v)
        return n
    }

    fun part2(input: List<String>): Int {
        val grid = toHeights(input)
        val rs = grid.indices
        val cs = grid.last().indices
        val sizes = mutableListOf<Int>()
        rs.forEach { r ->
            cs.forEach { c ->
                val count = grid.floodFill(r, c, 9)
                if (count > 0)
                    sizes += count
            }
        }
        val (xxl, xl, l) = sizes.sortedDescending()
        return xxl * xl * l
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 15)
        check(part2(testInput) == 1134)
    } catch (e: FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}


