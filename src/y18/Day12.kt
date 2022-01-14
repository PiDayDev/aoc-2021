package y18

private const val day = "12"

private data class State12(val string: String, val firstIndex: Int, val step: Int)


fun main() {
    fun List<String>.parse() = Pair(
        first().substringAfter("initial state: "),
        drop(2).map { it.split(" => ") }.associate { (a, b) -> a to b }
    )

    fun Map<String, String>.transform(s: String) =
        "....$s....".windowed(5).joinToString("") { this[it]!! }

    fun String.score(firstIndex: Number): Long =
        mapIndexed { index, c ->
            when (c) {
                '#' -> firstIndex.toLong() + index
                else -> 0L
            }
        }.sum()

    fun part1(input: List<String>): Long {
        val (initial, rules) = input.parse()
        val (result, firstIndex) = (1..20).fold(initial to 0) { (s, d), _ ->
            rules.transform(s) to d - 2
        }
        return result.score(firstIndex.toLong())
    }

    fun part2(input: List<String>): Long {
        val (initial, rules) = input.parse()
        val converged = generateSequence(1) { it + 1 }
            .scan(State12(string = initial, firstIndex = 0, step = 0)) { (s, index), step ->
                val next = rules.transform(s)
                val shortened = next.dropWhile { it == '.' }.dropLastWhile { it == '.' }
                val prefixLength = next.takeWhile { it == '.' }.count()
                State12(string = shortened, firstIndex = index - 2 + prefixLength, step = step)
            }
            .zipWithNext()
            .first { (a, b) -> a.string == b.string }
            .second
        val additionalSteps = 50_000_000_000L - converged.step
        return converged.string.score(additionalSteps + converged.firstIndex)

    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
