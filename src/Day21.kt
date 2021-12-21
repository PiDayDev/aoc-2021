import kotlin.math.min

private const val day = "21"


private data class Game(
    val p1: Int, val p2: Int,
    val score1: Int = 0, val score2: Int = 0,
    private val dice: Iterator<Int>, val rolls: Int = 0
) {
    fun increase(n: Int) = when (val v = (n + dice.next() + dice.next() + dice.next()) % 10) {
        0 -> 10
        else -> v
    }

    fun player1(): Game {
        val dest = increase(p1)
        return copy(p1 = dest, score1 = score1 + dest, rolls = rolls + 3)
    }

    fun player2(): Game {
        val dest = increase(p2)
        return copy(p2 = dest, score2 = score2 + dest, rolls = rolls + 3)
    }

    fun isOver() = score1 >= 1000 || score2 >= 1000

    override fun toString() =
        "| p1: $p1".padEnd(10) +
                "| p2: $p2".padEnd(10) +
                "| score1: $score1".padEnd(16) +
                "| score2: $score2".padEnd(16) +
                "| rolls: $rolls"
}

fun main() {
    fun part1(input: List<String>): Int {
        val dice: Iterator<Int> = generateSequence(1) { (it % 100) + 1 }.iterator()
        val (p1, p2) = input.map { it.substringAfter(": ").toInt() }
        val start = Game(p1 = p1, p2 = p2, dice = dice)

        println(start)

        val end = generateSequence(start) {
            val g1 = it.player1()
            val g2 = g1.player2()
            if (g1.isOver()) g1 else g2
        }
            .dropWhile { !it.isOver() }
            .first()

        println(end)

        return min(end.score1, end.score2) * end.rolls
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 1)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    val answer1 = part1(input)
    check(answer1 < 928620)
    println(answer1)
    println(part2(input))
}
