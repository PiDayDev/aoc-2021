package y18

private const val day = "01"

fun main() {
    fun part1(input: List<String>) =
        input.sumOf { it.toInt() }

    fun part2(input: List<String>): Int {
        val seen = mutableSetOf<Int>()
        val seq = generateSequence(input) { input }.flatten()
        return seq.scan(0) { total, curr -> total + curr.toInt() }
            .first { !seen.add(it) }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
