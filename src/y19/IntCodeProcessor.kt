package y19

open class IntCodeProcessor(
    initialCodes: List<Long>,
    private val stopAfterOutput: Boolean = false
) {
    private var codes: MutableMap<Long, Long> = initialCodes
        .mapIndexed { i, v -> i.toLong() to v }
        .toMap().toMutableMap()

    private var pointer = 0L

    private var halted = false

    private var relativeBase = 0L

    fun halted() = halted

    fun process(
        input: Iterator<Long>,
        output: (Long) -> Unit
    ) {
        while (!halted) {
            if (executeStep({ input.next() }, output)) break
        }
    }

    internal fun executeStep(
        input: () -> Long,
        output: (Long) -> Unit
    ): Boolean {
        val opCode = codes.at(pointer)
        val a = codes.at(pointer + 1)
        val b = codes.at(pointer + 2)
        val c = codes.at(pointer + 3)

        val types = (opCode / 100).toString()
        val modeA = types.getMode(0)
        val modeB = types.getMode(1)
        val modeC = types.getMode(2)

        val destA = addr(a, modeA)
        val destC = addr(c, modeC)

        when ((opCode % 100).toInt()) {
            1 -> {
                codes[destC] = codes.valueOf(a, modeA) + codes.valueOf(b, modeB)
                pointer += 4
            }
            2 -> {
                codes[destC] = codes.valueOf(a, modeA) * codes.valueOf(b, modeB)
                pointer += 4
            }
            3 -> {
                val read = input()
                //                    println("<=IN== $read")
                codes[destA] = read
                pointer += 2
            }
            4 -> {
                val write = codes.valueOf(a, modeA)
                //                    println("=OUT=> $write")
                output(write)
                pointer += 2
                if (stopAfterOutput) {
                    return true
                }
            }
            5 -> {
                if (0L != codes.valueOf(a, modeA)) {
                    pointer = codes.valueOf(b, modeB)
                } else {
                    pointer += 3
                }
            }
            6 -> {
                if (0L == codes.valueOf(a, modeA)) {
                    pointer = codes.valueOf(b, modeB)
                } else {
                    pointer += 3
                }
            }
            7 -> {
                codes[destC] = bool(codes.valueOf(a, modeA) < codes.valueOf(b, modeB))
                pointer += 4
            }
            8 -> {
                codes[destC] = bool(codes.valueOf(a, modeA) == codes.valueOf(b, modeB))
                pointer += 4
            }
            9 -> {
                relativeBase += codes.valueOf(a, modeA)
                pointer += 2
            }
            99 -> {
                halted = true
                return true
            }
        }
        return false
    }

    private fun addr(idx: Long, mode: Mode) = when (mode) {
        Mode.RELATIVE -> idx + relativeBase
        else -> idx
    }

    private fun Map<Long, Long>.at(idx: Long) = get(idx) ?: 0

    private fun Map<Long, Long>.valueOf(idx: Long, mode: Mode) = when (mode) {
        Mode.IMMEDIATE -> idx
        Mode.RELATIVE -> this[relativeBase + idx] ?: 0L
        Mode.POSITION -> this[idx] ?: 0L
    }

    private fun String.getMode(pos: Int) =
        when (getOrNull(length - 1 - pos)) {
            '1' -> Mode.IMMEDIATE
            '2' -> Mode.RELATIVE
            else -> Mode.POSITION
        }

    private fun bool(b: Boolean) = if (b) 1L else 0L
}

private enum class Mode {
    POSITION, IMMEDIATE, RELATIVE
}
