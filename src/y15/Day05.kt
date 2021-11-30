package y15

import java.io.FileNotFoundException

private const val day = "05"

/*
NICE
It contains a pair of any two letters that appears at least twice in the string without overlapping, like xyxy (xy) or aabcdefgaa (aa), but not like aaa (aa, but it overlaps).
It contains at least one letter which repeats with exactly one letter between them, like xyx, abcdefeghi (efe), or even aaa.
 */

fun main() {
    fun String.hasThreeVowels() = count { it in "aeiou" } > 2
    fun String.hasRepeat() = windowed(2).any { it[0] == it[1] }
    fun String.hasBlacklisted() = "ab" in this || "cd" in this || "pq" in this || "xy" in this

    fun part1(input: List<String>) = input.count { it.hasThreeVowels() && it.hasRepeat() && !it.hasBlacklisted() }

    fun String.firstRepeatGroup(): String? {
        for (i in 3 until length) {
            val s = "${get(i - 1)}${get(i)}"
            if (s in take(i - 1)) return s
        }
        return null
    }

    fun String.firstXYX() = windowed(3).firstOrNull { it[0] == it[2] }

    fun part2(input: List<String>) = input.count {
        it.firstRepeatGroup() != null && it.firstXYX() != null
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
