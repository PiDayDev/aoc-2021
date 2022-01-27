package y19

private const val day = "08"

fun main() {
    fun String.count(c: Char) = count { it == c }

    fun part1(input: List<String>): Int {
        val layers = input.first().chunked(25 * 6)
        val layer = layers.minByOrNull { it.count('0') }!!
        return layer.count('1') * layer.count('2')
    }

    fun part2(input: List<String>) {
        val layers = input.first().chunked(25 * 6)
        val merged = layers.reduce { over, under ->
            over.zip(under) { o, u -> if (o == '2') u else o }.joinToString("")
        }.replace('1','█').replace('0',' ')
        merged.chunked(25).forEach (::println)
    }

    val input = readInput("Day${day}")
    println(part1(input))
    part2(input)
}
