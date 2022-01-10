package y17

private const val day = 16

private infix fun List<String>.move(m: String): List<String> =
    when (m.take(1)) {
        "s" -> {
            val x = m.drop(1).toInt()
            takeLast(x) + dropLast(x)
        }
        "x" -> {
            val (a, b) = m.drop(1).split("/").map { it.toInt() }
            mapIndexed { j, s ->
                when (j) {
                    a -> get(b)
                    b -> get(a)
                    else -> s
                }
            }
        }
        "p" -> {
            val (a, b) = m.drop(1).split("/").map { indexOf(it) }
            move("x$a/$b")
        }
        else -> this
    }

private val startingDancers = "abcdefghijklmnop".toList().map { "$it" }

fun main() {
    fun part1(moves: List<String>) = moves
        .fold(startingDancers) { dancers, move -> dancers move move }
        .joinToString("")

    fun cycleLength(moves: List<String>) =
        generateSequence(startingDancers) { dancers -> moves.fold(dancers) { d, m -> d move m } }
            .scan(emptyList<List<String>>()) { acc, v -> acc + listOf(v) }
            .takeWhile { it.distinct().size == it.size }
            .last()
            .size

    fun part2(moves: List<String>): String {
        val cycleLength = cycleLength(moves)
        var dancers = startingDancers
        repeat(1_000_000_000 % cycleLength) {
            dancers = moves.fold(dancers) { d, m -> d move m }
        }
        return dancers.joinToString("")
    }

    val input = readInput("Day${day}")
    val moves = input.first().split(",")
    println(part1(moves))
    println(part2(moves))
}
