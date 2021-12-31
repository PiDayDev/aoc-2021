package y16

private const val input = "01110110101001000"

private fun String.generate(n: Int): String =
    if (length >= n)
        take(n)
    else
        "${this}0${reversed().map { if (it == '0') 1 else 0 }.joinToString("")}".generate(n)

private fun String.checksum(): String =
    if (length % 2 == 1)
        this
    else
        chunked(2).map { if (it[0] == it[1]) 1 else 0 }.joinToString("").checksum()

fun main() {
    fun part1() = input.generate(272).checksum()
    fun part2() = input.generate(35651584).checksum()

    println(part1())
    println(part2())
}
