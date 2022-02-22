package y20

import java.util.*

private const val day = "22"

private data class Combat(val p1: MutableList<Int>, val p2: MutableList<Int>) {
    fun isOver() = p1.isEmpty() || p2.isEmpty()

    fun score() = p1.score() + p2.score()

    fun fight(): Int {
        val card1 = p1.removeAt(0)
        val card2 = p2.removeAt(0)
        val win = card1 > card2
        if (win) {
            p1.add(card1)
            p1.add(card2)
        } else {
            p2.add(card2)
            p2.add(card1)
        }
        return if (win) 1 else 2
    }

    fun recursiveFight(): Int {
        val card1 = p1.removeAt(0)
        val card2 = p2.removeAt(0)
        val win = when {
            card1 <= p1.size && card2 <= p2.size -> {
                val sub1 = LinkedList(p1.take(card1))
                val sub2 = LinkedList(p2.take(card2))
                val (_, winner, history) = Combat(sub1, sub2).play(Combat::recursiveFight)
                history.toSet().size < history.size || winner == 1
            }
            else -> card1 > card2
        }
        if (win) {
            p1.addAll(sequenceOf(card1, card2))
        } else {
            p2.addAll(sequenceOf(card2, card1))
        }
        return if (win) 1 else 2
    }

    fun play(turn: Combat.() -> Int): Triple<Combat, Int, List<String>> =
        generateSequence(Triple(this, 0, listOf<String>())) { triple ->
            val (game, _, previously) = triple
            val hash = game.toString()
            val list = previously + hash
            if (hash in previously) {
                triple.copy(third = list)
            } else {
                val winner = turn.invoke(game)
                Triple(game, winner, list)
            }
        }.first { (game, _, list) ->
            val over = game.isOver()
            val seen = list.isNotEmpty() && list.last() in list.dropLast(1)
            over || seen
        }
}

private fun List<Int>.score() = mapIndexed { j, v -> (size - j) * v }.sum()

fun main() {
    fun List<String>.toCards() = mapNotNull { it.toIntOrNull() }.toMutableList()

    fun List<String>.toGame() = Combat(
        p1 = takeWhile { it.isNotBlank() }.toCards(),
        p2 = takeLastWhile { it.isNotBlank() }.toCards()
    )

    fun List<String>.getHighScore(rule: Combat.() -> Int) =
        toGame().play(rule).let { (game, _) -> game.score() }

    fun part1(input: List<String>) = input.getHighScore { fight() }

    fun part2(input: List<String>) = input.getHighScore { recursiveFight() }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 34127)

    val p2 = part2(input)
    println(p2)
    check(p2 == 32054)
}
