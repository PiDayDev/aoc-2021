package y17

private const val day = "06"

fun main() {
    fun List<String>.toBlocks() = this
        .flatMap { it.split("""\s+""".toRegex()) }
        .map { it.toInt() }

    fun List<Int>.reallocate(): List<Int> {
        val max = maxOf { it }
        val index = indexOf(max)
        val addToAll = max / size
        val rest = max % size
        val result = mapIndexed { j, v ->
            addToAll + if (j != index) v else 0
        }.toMutableList()
        (1..rest).forEach {
            result[(index + it) % size]++
        }
        return result.toList()
    }

    fun findDistinctAndFirstRepetition(input: List<String>): Pair<List<List<Int>>, List<Int>> {
        var list = input.toBlocks()
        val combos = mutableListOf<List<Int>>()
        while (list !in combos) {
            combos += list
            list = list.reallocate()
        }
        return combos to list
    }

    fun part1(sequence: List<List<Int>>) =
        sequence.size

    fun part2(sequence: List<List<Int>>, repetition: List<Int>) =
        sequence.size - sequence.indexOf(repetition)


    val input = readInput("Day${day}")

    val (sequence,repetition) = findDistinctAndFirstRepetition(input)
    println(part1(sequence))
    println(part2(sequence, repetition))
}
