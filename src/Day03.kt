private const val day = "03"

fun main() {
    fun List<String>.mostCommonBits() = first()
        .indices
        .map { j -> count { row -> row[j] == '1' } }
        .map { if (it * 2 >= size) 1 else 0 }

    fun List<String>.rate(compare: (Int, Int) -> Boolean) = indices
        .fold(this) { rest, pos ->
            if (rest.size == 1) rest
            else rest.mostCommonBits().let { bits -> rest.filter { compare(bits[pos], it[pos].digitToInt()) } }
        }
        .first()
        .toInt(2)

    fun List<String>.oxygenRate() = rate { a, b -> a == b }

    fun List<String>.co2Rate() = rate { a, b -> a != b }

    fun part1(input: List<String>): Int {
        val gammas = input.mostCommonBits()
        val gamma = gammas.joinToString("") { "$it" }.toInt(2)
        val epsilon = gammas.joinToString("") { "${1 - it}" }.toInt(2)
        return gamma * epsilon
    }

    fun part2(input: List<String>) = input.oxygenRate() * input.co2Rate()

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
