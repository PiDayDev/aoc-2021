package y19

private const val day = "02"

fun main() {
    fun process(start: List<Int>, noun: Int, verb: Int): Int {
        val codes = start.toMutableList()
        codes[1] = noun
        codes[2] = verb
        for (k in 0 until codes.size step 4) {
            val (o, a, b, c) = codes.subList(k, k + 4)
            when (o) {
                1 -> codes[c] = codes[a] + codes[b]
                2 -> codes[c] = codes[a] * codes[b]
                99 -> break
            }
        }
        return codes[0]
    }

    fun part1(input: List<String>): Int {
        val start = input.first().split(',').map { it.toInt() }
        return process(start, 12, 2)
    }


    fun part2(input: List<String>): Int {
        val start = input.first().split(',').map { it.toInt() }
        for (noun in 0..99) {
            for (verb in 0..99) {
                if (process(start, noun, verb) == 19690720)
                    return 100 * noun + verb
            }
        }
        return -1
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
