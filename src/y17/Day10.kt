package y17

private const val day = "10"

private fun List<Int>.flip(startIndex: Int, len: Int): List<Int> {
    val part = (this + this).subList(startIndex, startIndex + len).reversed()
    val diff = size - startIndex
    return if (len > diff) {
        part.drop(diff) + drop(len - diff).take(size - len) + part.take(diff)
    } else {
        take(startIndex) + part + drop(startIndex + len)
    }
}

private fun List<Int>.repeat(n: Int): List<Int> = when {
    n == 1 -> this
    n % 2 == 0 -> (this + this).repeat(n / 2)
    else -> throw IllegalArgumentException()
}

fun List<Int>.knotHash(): List<Int> {
    var current = 0
    var skip = 0
    return fold((0..255).toList()) { list, len ->
        val result = list.flip(current, len)
        current = (current + len + skip) % list.size
        skip++
        result
    }
}

fun List<Int>.knotHashHex() = knotHash()
    .chunked(16)
    .map { it.reduce { a, b -> a xor b } }
    .joinToString("") { it.toString(16).padStart(2, '0') }

fun main() {

    fun part1(input: List<String>) = input
        .first()
        .split(",")
        .map { it.toInt() }
        .knotHash()
        .let { it[0] * it[1] }

    fun part2(input: List<String>) =
        (input.first().map { it.code } + listOf(17, 31, 73, 47, 23))
            .repeat(64)
            .knotHashHex()

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
