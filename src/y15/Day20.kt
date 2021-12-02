package y15

import kotlin.math.max

private const val input = 29_000_000

// 🐢 very slow solution 🐌
//
// First run was with from1 = from2 = 1,
// then I plugged in values that are close to the actual answer
fun main() {

    val from1 = 665000
    fun presents1(house: Int) = 10 * (1..house).filter { house % it == 0 }.sum()
    fun part1(input: Int): Int = generateSequence(from1) { it + 1 }.first { presents1(it) >= input }
    println(part1(input))

    val from2 = 705000
    fun presents2(house: Int) = 11 * (max(1, house / 50)..house).filter { house % it == 0 }.sum()
    fun part2(input: Int): Int = generateSequence(from2) { it + 1 }.first { presents2(it) >= input }
    println(part2(input))

}
