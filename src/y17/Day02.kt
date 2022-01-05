package y17

private const val day = "02"


fun main() {
    val regex = """\s+""".toRegex()


    fun String.toSortedNumbers() =
        split(regex).map { it.toInt() }.sorted()

    fun List<Int>.division(): Int {
        for (i in indices) {
            val divisor = get(i)
            for (j in i + 1..indices.last) {
                val dividend = get(j)
                if (dividend % divisor == 0)
                    return dividend / divisor
            }
        }
        return 0
    }

    fun part1(input: List<String>) = input
        .map { it.toSortedNumbers() }
        .sumOf { it.last() - it.first() }

    fun part2(input: List<String>): Int {
        return input
            .map { it.toSortedNumbers() }
            .sumOf { it.division() }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
