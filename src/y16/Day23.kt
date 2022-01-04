package y16

private const val day = "23"

class AssembunnyProcess23(a: Int) :
    AssembunnyProcess("a" to a, "b" to 0, "c" to 0, "d" to 0) {

    override fun executeProgram(input: List<String>): Map<String, Int> {
        var p: AssembunnyProcess = this
        val program = input.toMutableList()
        while (p.pointer in program.indices) {
            val line = program[p.pointer]
            val (instr, param) = parse(line)
            p = when (instr) {
                "tgl" -> p.execToggle(param, program)
                else -> p.exec(line)
            }

        }
        return p.registers
    }

    private fun AssembunnyProcess.execToggle(
        param: String,
        program: MutableList<String>
    ): AssembunnyProcess {
        val row = pointer + valueOf(param)
        if (row in program.indices) {
            val (instr, p1, p2) = parse(program[row])
            val toggled = when (instr) {
                "inc" -> "dec"
                "dec", "tgl" -> "inc"
                "jnz" -> "cpy"
                else -> "jnz"
            }
            program[row] = "$toggled $p1 $p2"
        }
        return AssembunnyProcess(registers, pointer + 1)
    }

}

fun main() {
    fun part1(input: List<String>) = AssembunnyProcess23(7).executeProgram(input)["a"]!!

    fun smart2(a: Int): Int {
        // using the clues and logging some steps, the algorithm becomes clear
        return factorial(a) + 85 * 91
    }

    try {
        val testInput = readInput("Day${day}_test")
        val test1 = part1(testInput)
        check(test1 == 3) { "Test should be 3, instead it's $test1" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")

    println(part1(input))
    println(smart2(7))

    println(smart2(12))
}

private fun factorial(n: Int): Int = if (n < 2) 1 else n * factorial(n - 1)
