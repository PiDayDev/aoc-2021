package y20

private const val day = "10"

fun main() {
    fun part1(input: List<String>): Int {
        val jolts = input.map { it.toInt() }.sorted()
        val pairs = (listOf(0) + jolts + listOf(jolts.last() + 3)).windowed(2)
        val ones = pairs.count { (a, b) -> b - a == 1 }
        val threes = pairs.count { (a, b) -> b - a == 3 }
        return ones * threes
    }

    val cache = mutableMapOf<Pair<LongRange, List<Long>>, Long>()

    fun combos(range: LongRange, rest: List<Long>): Long {
        val from = range.first
        val till = range.last
        val key = range to rest
        val result = cache[key]
        if (result != null) return result
        val computed = rest
            .takeWhile { it in from + 1..from + 3 }
            .mapIndexed { j, v -> if (v >= till) 1L else combos(v..till, rest.drop(j + 1)) }
            .sum()
        cache[key] = computed
        return computed
    }

    fun part2(input: List<String>): Long {
        val jolts = input.map { it.toLong() }.sorted()
        val all = jolts + listOf(jolts.last() + 3)
        return combos(0..(jolts.last() + 3), all)
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 2343)
    val p2 = part2(input)
    println(p2)
    check(p2 == 31581162962944L)
}
