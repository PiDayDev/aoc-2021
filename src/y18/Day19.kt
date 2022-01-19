package y18

private const val day = 19

private data class Line(val i: String, val a: Int, val b: Int, val c: Int)

@Suppress("UNUSED_PARAMETER")
private data class Registers19(val registers: MutableList<Int>) {
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
    fun executeProgram(input: List<String>, init: Registers19): Int {
        var state = init
        val pointer = input.first().substringAfter("#ip ").toInt()
        val program = input.drop(1)
            .map { it.split(" ") }
            .map { (i, a, b, c) -> Line(i, a.toInt(), b.toInt(), c.toInt()) }

        var j = 0L
        while (true) {
            val idx = state.registers[pointer]
            if (idx !in program.indices) break
            val instruction = program[idx]
            state = state.exec(instruction)
            state.registers[pointer] = 1 + state.registers[pointer]

            if (++j % 12345678L == 476L)
                println("$j / $state")
        }
        return state.registers.first()
    }

    fun part1(input: List<String>): Int =
        executeProgram(input, Registers19(mutableListOf(0, 0, 0, 0, 0, 0)))

    @Suppress("UNUSED_VARIABLE")
    fun part2(): Long {
        // See Day19.js for a JS version of the instructions, where registries are named a,b,c,d,e,goto.

        // After the initial steps, the program enters a loop that can be summarized as
        //language=JavaScript
        val actualProgram = """
            do {
                b=1;
                do {
                    e = d*b;
                    if (e===c) a+=d;
                    b++;
                } while (b<=c);
                d++
            } while(d<=c);
        """.trimIndent()

        // This just adds to a (goal registry) all the divisors of value in registry c, which has value 10551277

        // so...
        val c = 10551277L
        return (1L..c).asSequence().filter { c % it == 0L }.sumOf { it }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2())
}

