package y17

private const val day = 23

private data class State23(
    val registers: Map<String, Long> = emptyMap(),
    val pointer: Int = 0
) {
    fun valueOf(s: String): Long = try {
        registers[s] ?: s.toLong()
    } catch (e: Exception) {
        0L
    }

    fun next() = copy(pointer = pointer + 1)

    fun next(register: Pair<String, Long>) = next().copy(registers = registers + register)
}

private class Process23(val registers: Map<String, Long>) {
    var finished = false
    val instructionCounters = mutableMapOf<String, Int>()

    fun executeProgram(
        initialState: State23? = null,
        program: List<String>
    ): State23 {
        var state = initialState ?: State23(registers)
        while (!finished) {
            state = execute(state, program[state.pointer])
            finished = state.pointer !in program.indices
        }
        return state
    }

    fun execute(state: State23, instruction: String): State23 {
        val (cmd, p1, p2) = "$instruction _".split(" ")
        val v1 = state.valueOf(p1)
        val v2 = state.valueOf(p2)
        val n = 1 + (instructionCounters[cmd] ?: 0)
        if (n % 1_000_000 == 0) println("${n / 1000000}.$cmd".padStart(9) + " | " + instruction.padEnd(12) + "| $state ")
        instructionCounters[cmd] = n
        return when (cmd) {
            "set" -> state.next(p1 to v2)
            "sub" -> state.next(p1 to v1 - v2)
            "mul" -> state.next(p1 to v1 * v2)
            "jnz" -> if (v1 != 0L) state.copy(pointer = state.pointer + v2.toInt()) else state.next()
            else -> state.next()
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val registers = "abcdefgh".associate { "$it" to 0L }
        val p = Process23(registers)
        p.executeProgram(program = input)
        return p.instructionCounters["mul"] ?: -1
    }

    /**
     * After many simplifications, the algorithm appears to be
     * counting the non-prime numbers in the range `(109300..(109300+1700) step 17)`
     */
    fun part2() = (109300..126300 step 17)
        .count { b ->
            (2 until b).any { b % it == 0 }
        }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2())
}
