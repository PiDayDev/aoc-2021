package y19

class IntCodeProcessor(
    initialCodes: List<Long>,
    private val stopAfterOutput: Boolean = false
) {
    private var codes: MutableMap<Long, Long> = initialCodes
        .mapIndexed { i, v -> i.toLong() to v }
        .toMap().toMutableMap()

    private var k = 0L
    private var halted = false

    fun halted() = halted

    fun process(
        input: Iterator<Long>,
        output: (Long) -> Unit
    ) {
        while (!halted) {
            val opCode = codes.at(k)
            val a = codes.at(k + 1)
            val b = codes.at(k + 2)
            val c = codes.at(k + 3)

            val types = (opCode / 100).toString()
            val immA = types.isImmediate(0)
            val immB = types.isImmediate(1)

            when ((opCode % 100).toInt()) {
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
                    if (stopAfterOutput) {
                        break
                    }
                }
                5 -> {
                    if (0L != codes.valueOf(a, immA)) {
                        k = codes.valueOf(b, immB)
                    } else {
                        k += 3
                    }
                }
                6 -> {
                    if (0L == codes.valueOf(a, immA)) {
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
                    halted = true
                    break
                }
            }
        }
    }

    private fun Map<Long,Long>.at(idx: Long) = get(idx) ?: 0

    private fun Map<Long,Long>.valueOf(idx: Long, immediate: Boolean) = if (immediate) idx else this[idx]!!

    private fun String.isImmediate(pos: Int) = getOrNull(length - 1 - pos) == '1'

    private fun bool(b: Boolean) = if (b) 1L else 0L
}
