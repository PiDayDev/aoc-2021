package y18

private const val day = "02"

fun main() {
    fun String.hasRepeat(n: Int) =
        groupBy { it }.any { (_, v) -> v.size == n }

    fun part1(input: List<String>) =
        input.count { it.hasRepeat(2) } * input.count { it.hasRepeat(3) }

    fun String.diff(s: String): List<Int> =
        zip(s).mapIndexedNotNull { index, (a, b) ->
            if (a != b) index else null
        }

    fun part2(input: List<String>): String {
        input.forEachIndexed { i, s ->
            (i + 1..input.indices.last).forEach { j ->
                val differences = input[j].diff(s)
                if (differences.size == 1)
                    return s.filterIndexed { k, _ -> k !in differences }
            }
        }
        return ""
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
