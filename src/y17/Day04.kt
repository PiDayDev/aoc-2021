package y17

private const val day = "04"

fun main() {
    fun part1(input: List<String>) = input.count {
        val tokens = it.split("""\s+""".toRegex())
        tokens.distinct().size == tokens.size
    }

    fun part2(input: List<String>) = input.count {
        val tokens = it.split("""\s+""".toRegex())
            .map { word -> word.toList().sorted() }
        tokens.distinct().size == tokens.size
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
