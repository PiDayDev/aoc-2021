package y16

private const val day = "12"

private class Process(
    val registers: Map<String, Int>,
    val pointer: Int = 0
) {
    constructor(vararg pairs: Pair<String, Int>) : this(registers = pairs.toMap())

    fun executeProgram(input: List<String>): Map<String, Int> {
        var p = this
        while (p.pointer in input.indices) {
            val line = input[p.pointer]
            p = p.exec(line)
        }
        return p.registers
    }

    private fun exec(line: String): Process {
        val (instr, param1, param2) = "$line 0".split(" ")
        val value1 = registers[param1] ?: param1.toInt()
        return when (instr) {
            "cpy" -> Process(registers + (param2 to value1), pointer + 1)
            "inc" -> Process(registers + (param1 to value1 + 1), pointer + 1)
            "dec" -> Process(registers + (param1 to value1 - 1), pointer + 1)
            "jnz" -> Process(
                registers,
                pointer + (if (value1 != 0) param2.toInt() else 1)
            )
            else -> throw IllegalArgumentException(instr)
        }
    }
}

fun main() {

    fun part1(input: List<String>) =
        Process("a" to 0, "b" to 0, "c" to 0, "d" to 0)
            .executeProgram(input)["a"]

    fun part2(input: List<String>) =
        Process("a" to 0, "b" to 0, "c" to 1, "d" to 0)
            .executeProgram(input)["a"]

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
