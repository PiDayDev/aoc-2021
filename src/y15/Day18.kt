package y15

private const val day = "18"

fun main() {
    fun List<String>.isLit(r: Int, c: Int) =
        try {
            this[r][c] == '#'
        } catch (e: Exception) {
            false
        }

    fun List<String>.litNeighbors(r: Int, c: Int): Int =
        listOf(
            r - 1 to c - 1,
            r - 1 to c,
            r - 1 to c + 1,
            r to c - 1,
            r to c + 1,
            r + 1 to c - 1,
            r + 1 to c,
            r + 1 to c + 1
        ).count { (row, col) -> isLit(row, col) }


    fun List<String>.gameOfLight() = mapIndexed { r, row ->
        row.mapIndexed { c, cell ->
            when (litNeighbors(r, c)) {
                3 -> "#"
                2 -> cell
                else -> "."
            }
        }.joinToString("")
    }

    fun List<String>.stuckLights() = mapIndexed { index, s ->
        if (index == indices.first || index == indices.last) "#" + s.drop(1).dropLast(1) + "#"
        else s
    }

    fun part1(input: List<String>): Int = (1..100)
        .fold(input) { grid, _ -> grid.gameOfLight() }
        .sumOf { row -> row.count { it == '#' } }

    fun part2(input: List<String>): Int = (1..100)
        .fold(input.stuckLights()) { grid, _ -> grid.gameOfLight().stuckLights() }
        .sumOf { row -> row.count { it == '#' } }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
