package y15

import java.io.FileNotFoundException

private const val day = "04"

fun main() {
    fun part1(input: List<String>): Int {
        val key = input.first()
        return generateSequence(1) { it + 1 }.first {
            val a = (key + it).md5b()
            a[0].toInt() == 0 && a[1].toInt() == 0 && a[2].toUByte().toString(16).length < 2
        }
    }

    fun part2(input: List<String>): Int {
        val key = input.first()
        return generateSequence(1) { it + 1 }.first {
            val a = (key + it).md5b()
            a[0].toInt() == 0 && a[1].toInt() == 0 && a[2].toInt() == 0
        }
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 1)
    } catch (e: FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
