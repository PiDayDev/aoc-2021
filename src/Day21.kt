import kotlin.math.max
import kotlin.math.min

private const val day = "21"

private interface Game {
    fun isOver(): Boolean
    fun player1(): Game
    fun player2(): Game
    fun play(): Game {
        println(this)

        val seq = generateSequence(this) {
            val g1 = it.player1()
            val g2 = g1.player2()
            if (g1.isOver()) g1 else g2
        }

        val end = seq.dropWhile { !it.isOver() }.first()

        println(end)
        return end
    }
}

fun main() {

    data class DeterministicGame(
        val p1: Int, val p2: Int,
        val score1: Int = 0, val score2: Int = 0,
        private val dice: Iterator<Int>, val rolls: Int = 0
    ) : Game {
        fun increase(n: Int) = when (val v = (n + dice.next() + dice.next() + dice.next()) % 10) {
            0 -> 10
            else -> v
        }

        override fun player1(): DeterministicGame {
            val dest = increase(p1)
            return copy(p1 = dest, score1 = score1 + dest, rolls = rolls + 3)
        }

        override fun player2(): DeterministicGame {
            val dest = increase(p2)
            return copy(p2 = dest, score2 = score2 + dest, rolls = rolls + 3)
        }

        override fun isOver() = score1 >= 1000 || score2 >= 1000

        override fun play() = super.play() as DeterministicGame

        override fun toString() = "" +
                "| p1: $p1".padEnd(10) +
                "| p2: $p2".padEnd(10) +
                "| score1: $score1".padEnd(16) +
                "| score2: $score2".padEnd(16) +
                "| rolls: $rolls"
    }

    fun part1(input: List<String>): Int {
        val (p1, p2) = input.map { it.substringAfter(": ").toInt() }
        val end = DeterministicGame(p1 = p1, p2 = p2, dice = generateSequence(1) { (it % 100) + 1 }.iterator()).play()
        return min(end.score1, end.score2) * end.rolls
    }

    val diracSteps = mapOf(3 to 1L, 4 to 3L, 5 to 6L, 6 to 7L, 7 to 6L, 8 to 3L, 9 to 1L)

    data class DiracState(
        val universes: Long = 1L,
        val position1: Int,
        val position2: Int,
        val score1: Long = 0L,
        val score2: Long = 0L,
    ) {
        fun player1(): List<DiracState> = diracSteps
            .map { (increment, multiplier) ->
                val pos = when (val v = (position1 + increment) % 10) {
                    0 -> 10
                    else -> v
                }
                copy(
                    universes = universes * multiplier,
                    position1 = pos,
                    score1 = score1 + pos
                )
            }

        fun player2(): List<DiracState> = diracSteps
            .map { (increment, multiplier) ->
                val pos = when (val v = (position2 + increment) % 10) {
                    0 -> 10
                    else -> v
                }
                copy(
                    universes = universes * multiplier,
                    position2 = pos,
                    score2 = score2 + pos
                )
            }

        override fun toString() = "<$universes uni @ $position1/$position2 | ${score1}/${score2}pts>"
    }

    data class Dirac(
        val states: List<DiracState> = emptyList(),
        val win1: Long = 0L,
        val win2: Long = 0L
    ) : Game {
        constructor(start1: Int, start2: Int) : this(
            states = listOf(DiracState(position1 = start1, position2 = start2))
        )

        override fun player1(): Dirac {
            val newP1 = states.flatMap { it.player1() }
            val (wins, rest) = newP1.partition { it.score1 >= 21 }
            return copy(states = rest, win1 = win1 + wins.sumOf { it.universes })
        }

        override fun player2(): Dirac {
            val newP2 = states.flatMap { it.player2() }
            val (wins, rest) = newP2.partition { it.score2 >= 21 }
            return copy(states = rest, win2 = win2 + wins.sumOf { it.universes })
        }

        override fun isOver() = states.isEmpty()

        override fun play() = super.play() as Dirac

        override fun toString() = "Dirac Wins: $win1 VS $win2"
    }


    fun part2(input: List<String>): Long {
        val (p1, p2) = input.map { it.substringAfter(": ").toInt() }
        val end = Dirac(p1, p2).play()
        return max(end.win1, end.win2)
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
