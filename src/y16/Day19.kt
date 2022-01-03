package y16

import kotlin.math.log
import kotlin.math.pow

private const val elves = 3004953

fun main() {
    fun Pair<List<Int>, Boolean>.steal1(): Pair<List<Int>, Boolean> {
        val list = first.filterIndexed { i, _ -> (i % 2 == 0) == second }
        val odd = list.last() == first.last()
        return list to !odd
    }

    fun part1(count: Int): Int {
        var curr = (1..count).toList() to true
        while (curr.first.size > 1) {
            curr = curr.steal1()
        }
        return curr.first.last()
    }

    /* By induction after computing on the numbers until 3^5 or so */
    fun part2(n: Int): Int {
        val k = log(n.toDouble(), 3.0).toInt()
        val tk = 3.0.pow(k).toInt()
        return when (n) {
            tk -> n
            in (tk + 1..tk * 2) -> n - tk
            else -> 2 * n - 3 * tk
        }
    }

    println(part1(elves))
    println(part2(elves))
}
