private const val day = "04"

private class Board(val rows: List<List<Int>>) {
    constructor(lines: List<String>, base: Int) : this(lines
        .dropWhile { it.isBlank() }
        .dropLastWhile { it.isBlank() }
        .map { it.trim().split(" +".toRegex()) }
        .map { it.map { n -> n.toInt(base) } }
    )

    override fun toString() = rows.joinToString("\n") { r -> r.joinToString(":") { "$it" } }

    fun wins(chosen: List<Int>): Boolean {
        if (rows.any { chosen.containsAll(it) }) return true
        val cols = rows.first().indices
        if (cols.any { chosen.containsAll(rows.map { r -> r[it] }) }) return true
        return false
    }
}

fun main() {
    fun toNumbersAndBoards(input: List<String>): Pair<List<Int>, List<Board>> {
        val numbers = input.first().split(",").map { it.toInt() }

        val boards = input
            .drop(1)
            .dropLastWhile { it.isBlank() }
            .chunked(6)
            .map { Board(it, 10) }
        return Pair(numbers, boards)
    }

    fun part1(input: List<String>): Int {
        val (numbers, boards) = toNumbersAndBoards(input)

        for (j in numbers.indices) {
            val chosen = numbers.take(1 + j)
            val winner = boards.firstOrNull { it.wins(chosen) }
            if (winner != null) {
                return chosen.last() * (winner.rows.flatten() - chosen.toSet()).sum()
            }
        }

        return -1
    }

    fun part2(input: List<String>): Int {
        val (numbers, boards) = toNumbersAndBoards(input)

        var rest = boards

        for (j in numbers.indices) {
            val chosen = numbers.take(1 + j)
            val winners = rest.filter { it.wins(chosen) }
            rest = rest - winners.toSet()
            if (rest.isEmpty()) {
                return chosen.last() * (winners.last().rows.flatten() - chosen.toSet()).sum()
            }
        }
        return -2
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
