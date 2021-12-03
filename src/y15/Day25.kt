package y15

private const val day = "25"

fun main() {
    fun Long.nextCode() = this * 252533L % 33554393

    fun toIndex(row: Int, col: Int): Int {
        // last in previous diagonal
        val rowOfLast = 1
        val colOfLast = row + col - 1 - rowOfLast
        val posOfLast = colOfLast * (colOfLast + 1) / 2
        return posOfLast + col
    }

    fun part1(row: Int, col: Int) =
        generateSequence(20151125L) { it.nextCode() }
            .drop(toIndex(row, col) - 1)
            .first()

    val input = readInput("Day${day}")
    val (row, col) = input.first().dropLast(1)
        .substringAfter("at row ").split(", column ")
        .map { it.toInt() }

    println(part1(row, col))
}
