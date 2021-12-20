private const val day = "20"

fun main() {
    val input = readInput("Day${day}")

    fun getChars(row: String, pos: Int, external: Char): String {
        val x = if (pos - 1 in row.indices) row[pos - 1] else external
        val y = if (pos in row.indices) row[pos] else external
        val z = if (pos + 1 in row.indices) row[pos + 1] else external
        return "$x$y$z"
    }

    fun List<String>.evolve(rules: String, external: Char) =
        mapIndexed { r, row ->
            val pre = if (r - 1 in indices) this[r - 1] else row
            val suf = if (r + 1 in indices) this[r + 1] else row
            row.mapIndexed { c, _ ->
                val str = getChars(pre, c, external) + getChars(row, c, external) + getChars(suf, c, external)
                val num = str.replace('.', '0').replace('#', '1').toInt(2)
                rules[num]
            }.joinToString("") { "$it" }
        }


    fun toExpandedGrid(input: List<String>, iterations: Int): Pair<String, List<String>> {
        val rules = input.first()
        val grid = input.takeLastWhile { it.isNotBlank() }

        val extraRow = grid.first().replace('#', '.')
        val extraRows = List(iterations) { extraRow }
        val expandedGrid = (extraRows + grid + extraRows).map { ".".repeat(iterations) + it + ".".repeat(iterations) }
        return rules to expandedGrid
    }

    fun solve(input: List<String>, iterations: Int): Int {
        val (rules, expandedGrid) = toExpandedGrid(input, iterations)
        val externalChars = generateSequence('.') { if (it == '.') rules.first() else rules.last() }
        return externalChars
            .take(iterations)
            .fold(expandedGrid) { it, c -> it.evolve(rules, external = c) }
            .sumOf { row -> row.count { it == '#' } }
    }

    println(solve(input, 2))
    println(solve(input, 50))
}
