import java.util.*

private const val day = "10"

fun main() {

    val pairs = listOf("()", "[]", "{}", "<>")
        .flatMap { s -> listOf(s[0] to s[1], s[1] to s[0]) }
        .toMap()

    val corruptionScores = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    )

    fun corruptionScore(line: String): Int {
        val stack = Stack<Char>()
        for (c in line) {
            when (c) {
                ')', ']', '}', '>' -> if (stack.pop() != pairs[c]) return corruptionScores[c]!!
                '(', '[', '{', '<' -> stack.push(c)
            }
        }
        return 0
    }

    fun part1(input: List<String>) = input.sumOf { corruptionScore(it) }

    fun complete(line: String): String {
        val stack = Stack<Char>()
        for (c in line) {
            when (c) {
                ')', ']', '}', '>' -> stack.pop()
                '(', '[', '{', '<' -> stack.push(c)
            }
        }
        return stack.joinToString("").reversed()
    }

    val completionScores = mapOf(
        ')' to '1',
        ']' to '2',
        '}' to '3',
        '>' to '4',
    )

    fun String.completionScore() = map { pairs[it] }
        .joinToString("") { "${completionScores[it]}" }
        .toLong(5)

    fun part2(input: List<String>) = input
        .filter { corruptionScore(it) == 0 }
        .map { complete(it) }
        .map { it.completionScore() }
        .let { it.sorted()[it.size / 2] }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
