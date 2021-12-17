package y16

private const val day = "10"

data class State(
    val bots: Map<Int, List<Int>>,
    val actions: Map<Int, Pair<String, String>>,
    val outputs: Map<Int, List<Int>> = emptyMap(),
) {
    fun step(): State {
        val (giver, chips) = bots.filter { it.value.size == 2 }.toList().first()
        val (lo, hi) = chips.sorted()
        val (loReceiver, hiReceiver) = actions[giver]!!
        val loId = loReceiver.substringAfter(" ").toInt()
        val hiId = hiReceiver.substringAfter(" ").toInt()

        val newBots = bots.toMutableMap()
        val newOut = outputs.toMutableMap()

        val loMap = if (loReceiver.startsWith("output")) newOut else newBots
        loMap[loId] = (loMap[loId] ?: emptyList()) + lo

        val hiMap = if (hiReceiver.startsWith("output")) newOut else newBots
        hiMap[hiId] = (hiMap[hiId] ?: emptyList()) + hi

        newBots.remove(giver)

        return copy(bots = newBots, outputs = newOut)
    }
}

fun main() {
    fun setup(input: List<String>): State {
        val actions = input
            .filter { "gives" in it }
            .associate { row ->
                val (bot, lo, hi) = row.drop(4).split(""" \w+ \w+ to """.toRegex())/*.map { it.toInt() }*/
                bot.toInt() to (lo to hi)
            }
        val bots = input
            .filter { "value" in it }
            .map { row ->
                val (value, bot) = row.substringAfter("value ").split(" goes to bot ").map { it.toInt() }
                bot to value
            }
            .fold(mapOf<Int, List<Int>>()) { map, (bot, value) ->
                map + (bot to (map[bot] ?: emptyList()) + value)
            }
        return State(bots, actions)
    }


    fun part1(input: List<String>): Int {
        fun Collection<Int>.isInteresting() = toSet() == setOf(61, 17)

        val state = setup(input)
        val finalState = generateSequence(state) {
            it.step()
        }.first {
            it.bots.any { (_, v) -> v.isInteresting() }
        }
        return finalState.bots.toList().first { it.second.isInteresting() }.first
    }

    fun part2(input: List<String>): Int {
        val state = setup(input)
        val finalState = generateSequence(state) {
            it.step()
        }.first {
            it.outputs.keys.containsAll(setOf(0, 1, 2))
        }
        val outputs = finalState.outputs.toList().filter { it.first in 0..2 }.map { it.second.last() }
        return outputs.fold(1) { a, b -> a * b }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
