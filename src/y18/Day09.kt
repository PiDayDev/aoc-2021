package y18

private const val day = "09"

private data class Marbles(var current: Long) {

    val marbles: MutableMap<Long, Long> = mutableMapOf(0L to 0L)
    val selbmar: MutableMap<Long, Long> = mutableMapOf(0L to 0L)

    fun insert(n: Long): Long =
        if (n % 23L == 0L) {
            val backwards = (1..8).scan(current) { it, _ -> selbmar[it]!! }
            val prev = backwards.last()
            val toRemove = backwards.dropLast(1).last()
            current = marbles[toRemove]!!
            marbles[prev] = current
            marbles -= toRemove
            selbmar[current] = prev
            n + toRemove
        } else {
            val next1 = marbles[current]!!
            val next2 = marbles[next1]!!
            marbles[next1] = n
            marbles[n] = next2
            selbmar[n]=next1
            selbmar[next2]=n
            current = n
            0
        }

}

fun main() {
    fun winnerScore(players: Int, last: Long): Long {
        val marbles = Marbles(0)
        val total = (0 until players).associateWith { 0L }.toMutableMap()
        var elf = 0
        (1..last).forEach { marble ->
            val score = marbles.insert(marble)
            total[elf % players] = total[elf % players]!! + score
            elf++
        }
        return total.maxOf { it.value }
    }

    fun part1(input: List<String>): Long {
        val (players, last) = input.first().split(" ").let { it[0].toInt() to it[6].toLong() }
        return winnerScore(players, last)
    }

    fun part2(input: List<String>): Long {
        val (players, last) = input.first().split(" ").let { it[0].toInt() to it[6].toInt() }
        return winnerScore(players, last * 100L)
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
