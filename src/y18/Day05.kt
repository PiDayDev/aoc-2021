package y18

private const val day = "05"

val xX = ('a'..'z').map { "${it.lowercase()}${it.uppercase()}" }
val Xx = ('a'..'z').map { "${it.uppercase()}${it.lowercase()}" }
private fun String.react1() = xX.fold(this) { it, s -> it.replace(s, "") }
private fun String.react2() = Xx.fold(this) { it, s -> it.replace(s, "") }

fun main() {

    fun part1(input: String): Int {
        var x = input
        while (true) {
            val tmp = x.react1().react2()
            if (tmp == x) break
            x = tmp
        }
        return x.length
    }

    fun part2(input: String) =
        ('a'..'z').map { input.replace(it.lowercase(), "").replace(it.uppercase(), "") }.minOf { part1(it) }

    val input = readInput("Day${day}").joinToString("")
    println(part1(input))
    println(part2(input))
}
