package y18

private const val day = 19

private data class Line(val i: String, val a: Int, val b: Int, val c: Int)

@Suppress("UNUSED_PARAMETER")
private data class Registers19(val registers: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0)) {
    fun set(register: Int, value: Int): Registers19 = registers
        .toMutableList()
        .also { it[register] = value }
        .let { Registers19(it) }

    fun get(register: Int) = registers[register]

    private fun bool(b: Boolean) = if (b) 1 else 0

    fun exec(s: String, a: Int, b: Int, c: Int) =
        when (s) {
            "addr" -> set(c, get(a) + get(b))
            "addi" -> set(c, get(a) + b)
            "mulr" -> set(c, get(a) * get(b))
            "muli" -> set(c, get(a) * b)
            "banr" -> set(c, get(a) and get(b))
            "bani" -> set(c, get(a) and b)
            "borr" -> set(c, get(a) or get(b))
            "bori" -> set(c, get(a) or b)
            "setr" -> set(c, get(a))
            "seti" -> set(c, a)
            "gtir" -> set(c, bool(a > get(b)))
            "gtri" -> set(c, bool(get(a) > b))
            "gtrr" -> set(c, bool(get(a) > get(b)))
            "eqir" -> set(c, bool(a == get(b)))
            "eqri" -> set(c, bool(get(a) == b))
            "eqrr" -> set(c, bool(get(a) == get(b)))
            else -> this
        }

    fun exec(line: Line) = exec(line.i, line.a, line.b, line.c)
}

fun main() {
    fun part1(input: List<String>): Int {
        val pointer = input.first().substringAfter("#ip ").toInt()
        val program = input.drop(1)
            .map { it.split(" ") }
            .map { (i, a, b, c) -> Line(i, a.toInt(), b.toInt(), c.toInt()) }

        var state = Registers19()
        while (true) {
            val idx = state.registers[pointer]
            if (idx !in program.indices) break
            state = state.exec(program[idx])
            state.registers[pointer]=1+state.registers[pointer]
        }
        return state.registers.first()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 1)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
