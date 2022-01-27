package y19

private const val day = "05"

class IntCodeProcessor5 {
    fun process(
        startCodes: List<Int>,
        input: Iterator<Int>,
        output: (Int) -> Unit
    ): Int {
        val codes = startCodes.toMutableList()
        var k = 0
        while (k in codes.indices) {
            val opCode = codes.at(k)
            val a = codes.at(k + 1)
            val b = codes.at(k + 2)
            val c = codes.at(k + 3)

            val types = (opCode / 100).toString()
            val immA = types.isImmediate(0)
            val immB = types.isImmediate(1)

            when (opCode % 100) {
                1 -> {
                    codes[c] = codes.valueOf(a, immA) + codes.valueOf(b, immB)
                    k += 4
                }
                2 -> {
                    codes[c] = codes.valueOf(a, immA) * codes.valueOf(b, immB)
                    k += 4
                }
                3 -> {
                    codes[a] = input.next()
                    k += 2
                }
                4 -> {
                    output(codes.valueOf(a, immA))
                    k += 2
                }
                5 -> {
                    if (0 != codes.valueOf(a, immA)) {
                        k = codes.valueOf(b, immB)
                    } else {
                        k += 3
                    }
                }
                6 -> {
                    if (0 == codes.valueOf(a, immA)) {
                        k = codes.valueOf(b, immB)
                    } else {
                        k += 3
                    }
                }
                7 -> {
                    codes[c] = bool(codes.valueOf(a, immA) < codes.valueOf(b, immB))
                    k += 4
                }
                8 -> {
                    codes[c] = bool(codes.valueOf(a, immA) == codes.valueOf(b, immB))
                    k += 4
                }
                99 -> {
                    break
                }
            }
        }
        return codes[0]
    }

    private fun String.isImmediate(pos: Int) =
        getOrNull(length - 1 - pos) == '1'

    private fun List<Int>.valueOf(idx: Int, immediate: Boolean) =
        if (immediate) idx else this[idx]

    fun List<Int>.at(idx: Int) = getOrNull(idx) ?: 0

    private fun bool(b: Boolean) = if (b) 1 else 0
}

fun main() {

    fun List<String>.execute(systemId: Int): Int {
        val codes = joinToString("").split(",").map { it.toInt() }
        val outputs = mutableListOf<Int>()
        IntCodeProcessor5().process(codes, sequenceOf(systemId).iterator(), outputs::add)
        println(outputs)
        return outputs.last()
    }

    fun part1(input: List<String>): Int = input.execute(1)
    fun part2(input: List<String>): Int = input.execute(5)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
