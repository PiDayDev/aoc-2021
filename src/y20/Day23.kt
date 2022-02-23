package y20

private const val day = "23"

fun main() {
    fun MutableMap<Int, Int>.play(
        turns: Int,
        current: Int,
    ) {
        val min: Int = minOf { it.key }
        val max: Int = maxOf { it.key }
        var curr = current
        repeat(turns) {
            val a = get(curr)!!
            val b = get(a)!!
            val c = get(b)!!
            val after = get(c)!!
            var dest = curr - 1
            while (true) {
                when (dest) {
                    !in min..max -> dest = max
                    a, b, c -> dest--
                    else -> break
                }
            }
            this[curr] = after
            this[c] = this[dest]!!
            this[dest] = a
            curr = after
        }
    }

    fun part1(input: List<Int>): String {
        val successors: MutableMap<Int, Int> =
            input.run { (zipWithNext().toMap() + (last() to first())) }.toMutableMap()

        val cups = successors.apply { play(100, current = input.first()) }
        return generateSequence(1) { cups[it] }
            .drop(1)
            .takeWhile { it != 1 }
            .joinToString("")
    }

    fun part2(input: List<Int>): Long {
        val total = 1_000_000
        val turns = 10_000_000

        val firstExtra = input.maxOf { it } + 1

        val start = input.run { zipWithNext().toMap() }
        val extra = (firstExtra until total).map { it to it + 1 }

        val successors = start + extra + (input.last() to firstExtra) + (total to input.first())
        val cups = successors
            .toMutableMap()
            .apply { play(turns, current = input.first()) }

        return cups[1]!!.let { it.toLong() * cups[it]!! }
    }

    val input = readInput("Day$day").joinToString("").toList().map { it.digitToInt() }

    val p1 = part1(input)
    println(p1)
    check(p1 == "35827964")
    val p2 = part2(input)
    println(p2)
    check(p2 == 5403610688L)
}
