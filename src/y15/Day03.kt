package y15

import java.io.FileNotFoundException

private const val day = "03"

fun main() {
    fun follow(instructions: String): Set<Pair<Int, Int>> {
        val visited = mutableSetOf<Pair<Int, Int>>()
        instructions.fold(0 to 0) { acc, k ->
            visited.add(acc)
            val (c, r) = acc
            when (k) {
                '^' -> c to r - 1
                'v' -> c to r + 1
                '>' -> c + 1 to r
                '<' -> c - 1 to r
                else -> acc
            }
        }
        return visited
    }

    fun part1(input: List<String>): Int {
        val instructions = input.first()
        val visited = follow(instructions)
        return visited.size
    }

    fun part2(input: List<String>): Int {
        val instructions = input.first()
        val santa = instructions.filterIndexed { index, _ -> index % 2 == 0 }
        val robot = instructions.filterIndexed { index, _ -> index % 2 > 0 }
        return (follow(santa) + follow(robot)).size
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
