package y20

private const val day = "18"

private val simpleExpression = """\([^)(]+\)""".toRegex()
private val spaces = """\s+""".toRegex()

fun main() {
    fun String.leftToRight(): Long {
        val tokens = split(spaces)
        val initial = tokens.first().toLong()
        val rest = tokens.drop(1)
        return rest.chunked(2).fold(initial) { acc, next ->
            val (op, num) = next
            val n = num.toLong()
            if (op == "+") acc + n else acc * n
        }
    }

    fun String.solve(simplify: String.() -> Long): Long =
        if (contains("(")) {
            replace(simpleExpression) {
                "${it.value.removeSurrounding("(", ")").simplify()}"
            }.solve(simplify)
        } else {
            simplify()
        }

    fun MutableList<String>.splice(around: Int, op: (Long, Long) -> Long) {
        val b = removeAt(around + 1)
        removeAt(around)
        val a = removeAt(around - 1)
        add(around - 1, op(a.toLong(), b.toLong()).toString())
    }

    fun String.weirdPrecedence(): Long {
        val tokens = split(spaces).toMutableList()
        var plus = tokens.indexOf("+")
        while (plus >= 0) {
            tokens.splice(plus) { a, b -> a + b }
            plus = tokens.indexOf("+")
        }
        var times = tokens.indexOf("*")
        while (times >= 0) {
            tokens.splice(times) { a, b -> a * b }
            times = tokens.indexOf("*")
        }
        check(tokens.size == 1)
        return tokens.first().toLong()
    }

    fun part1(input: List<String>) = input.sumOf { it.solve(String::leftToRight) }

    fun part2(input: List<String>) = input.sumOf { it.solve(String::weirdPrecedence) }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 23507031841020L)
    val p2 = part2(input)
    println(p2)
    check(p2 == 218621700997826L)
}
