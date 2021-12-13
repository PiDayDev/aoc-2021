package y16

private const val day = "03"

fun main() {
    fun String.toTriangle() = split("""\s+""".toRegex()).filter { it.isNotBlank() }.map { it.toInt() }

    fun List<Int>.isPossible(): Boolean {
        val (a, b, c) = this
        return a + b > c && b + c > a && c + a > b
    }

    fun part1(input: List<String>) = input.count { it.toTriangle().isPossible() }

    fun part2(input: List<String>) = input
        .map { it.toTriangle() }
        .chunked(3)
        .sumOf { (r1, r2, r3) -> (0..2).count { listOf(r1[it], r2[it], r3[it]).isPossible() } }


    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))

}
